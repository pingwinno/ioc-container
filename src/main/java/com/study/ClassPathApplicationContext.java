package com.study;

import com.study.model.Bean;
import com.study.model.BeanDefinition;
import com.study.reader.BeanDefinitionReader;

import java.util.ArrayList;
import java.util.List;

public class ClassPathApplicationContext implements ApplicationContext {

    private final BeanDefinitionReader beanDefinitionReader;
    private final List<Bean> beans = new ArrayList<>();

    public ClassPathApplicationContext(BeanDefinitionReader beanDefinitionReader) {
        this.beanDefinitionReader = beanDefinitionReader;
    }

    @Override
    public Object getBean(String id) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> tClass) {
        return null;
    }

    @Override
    public <T> T getBean(String id, Class<T> tClass) {
        return null;
    }

    @Override
    public List<String> getBeanNames() {
        return null;
    }

    @Override
    public void loadContext(BeanDefinitionReader beanDefinitionReader) {

    }

    private List<BeanDefinition> getBeanDefinitions() {
        return null;
    }

    private void injectValueDependencies(List<BeanDefinition> beanDefinitions, List<Bean> beans) {

    }

    private void injectRefDependencies(List<BeanDefinition> beanDefinitions, List<Bean> beans) {

    }
}
