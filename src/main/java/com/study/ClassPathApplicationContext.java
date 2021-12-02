package com.study;

import com.study.model.Bean;
import com.study.model.BeanDefinition;
import com.study.reader.BeanDefinitionReader;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClassPathApplicationContext implements ApplicationContext {

    private final BeanDefinitionReader beanDefinitionReader;
    private final List<Bean> beans;

    public ClassPathApplicationContext(BeanDefinitionReader beanDefinitionReader) {
        this.beanDefinitionReader = beanDefinitionReader;
        var beanDefinitions = getBeanDefinitions();
        beans = createBeans(beanDefinitions);
        injectValueDependencies(beanDefinitions, beans);
        injectRefDependencies(beanDefinitions, beans);
    }

    @Override
    public Object getBean(String id) {
        for (Bean bean : beans) {
            if (Objects.equals(bean.getId(), id)) {
                return bean.getValue();
            }
        }
        return null;
    }


    @Override
    public <T> T getBean(Class<T> tClass) {
        var isBeanUnique = false;
        T beanToReturn = null;
        for (Bean bean : beans) {
            var value = bean.getValue();
            if (Objects.equals(value.getClass(), tClass)) {
                if (isBeanUnique) {
                    throw new IllegalStateException(
                            "Cannot get bean by class. Beans of class " + tClass + " more than one.");
                }
                beanToReturn = tClass.cast(bean.getValue());
                isBeanUnique = true;
            }
        }
        return beanToReturn;
    }

    @Override
    public <T> T getBean(String id, Class<T> tClass) {
        return tClass.cast(getBean(id));
    }

    @Override
    public List<String> getBeanNames() {
        return null;
    }

    private List<BeanDefinition> getBeanDefinitions() {
        return beanDefinitionReader.readBeanDefinitions();
    }

    private List<Bean> createBeans(List<BeanDefinition> beanDefinitions) {
        return beanDefinitions.parallelStream()
                              .map(this::createBean)
                              .collect(Collectors.toList());
    }

    private void injectValueDependencies(List<BeanDefinition> beanDefinitions, List<Bean> beans) {
        var definitionMap = beanDefinitions.stream()
                                           .collect(Collectors.toMap(BeanDefinition::getId, Function.identity()));
        beans.stream()
             .filter(bean -> definitionMap.containsKey(bean.getId()))
             .forEach(bean -> injectFields(definitionMap.get(bean.getId())
                                                        .getValueDependencies(), bean));
    }

    private void injectRefDependencies(List<BeanDefinition> beanDefinitions, List<Bean> beans) {
        var definitionMap = beanDefinitions.stream()
                                           .collect(Collectors.toMap(BeanDefinition::getId, Function.identity()));
        beans.stream()
             .filter(bean -> definitionMap.containsKey(bean.getId()))
             .forEach(bean -> injectBeans(definitionMap.get(bean.getId())
                                                       .getRefDependencies(), bean));
    }

    @SneakyThrows
    private Bean createBean(BeanDefinition beanDefinition) {
        var classObject = Class.forName(beanDefinition.getClassName());
        var instance = classObject.getDeclaredConstructor()
                                  .newInstance();
        return Bean.builder()
                   .id(beanDefinition.getId())
                   .value(instance)
                   .build();
    }

    @SneakyThrows
    private void injectFields(Map<String, String> fieldsMap, Bean bean) {
        var object = bean.getValue();
        var superClasses = new ArrayList<Class<?>>();
        //брать все приватные поля в иерархии не очень хорошая идея, но мне очень хотелось инжектить в Hikari а не писать враппер
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            superClasses.add(clazz);
            clazz = clazz.getSuperclass();
        }
        var fieldsToInject = superClasses.stream()
                                         .flatMap(c -> Arrays.stream(c.getDeclaredFields()))
                                         .filter(field -> fieldsMap.containsKey(field.getName()))
                                         .collect(Collectors.toList());
        for (Field field : fieldsToInject) {
            setField(field, object, fieldsMap.get(field.getName()));
        }
    }

    @SneakyThrows
    private void injectBeans(Map<String, String> fieldsMap, Bean bean) {
        var object = bean.getValue();
        var fieldsToInject = Arrays.stream(object.getClass()
                                                 .getDeclaredFields())
                                   .filter(field -> fieldsMap.containsKey(field.getName()))
                                   .collect(Collectors.toList());
        for (Field field : fieldsToInject) {
            setField(field, object, getBean(field.getName()));
        }
    }

    @SneakyThrows
    private void setField(Field field, Object object, Object value) {
        field.setAccessible(true);
        field.set(object, value);
    }
}
