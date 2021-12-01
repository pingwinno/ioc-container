package com.study;

import com.study.reader.BeanDefinitionReader;

import java.util.List;

public interface ApplicationContext {
    Object getBean(String id);

    <T> T getBean(Class<T> tClass);

    <T> T getBean(String id, Class<T> tClass);

    List<String> getBeanNames();
}
