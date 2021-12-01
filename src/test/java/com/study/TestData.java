package com.study;

import com.study.model.BeanDefinition;

import java.util.List;
import java.util.Map;

public class TestData {
    public static final String KEY_FIELD = "database";
    public static final String VALUE_FIELD = "h2";
    public static final String KEY_REF = "dao";
    public static final BeanDefinition DAO_BEAN_DEFINITION = BeanDefinition.builder()
                                                                           .id("dao")
                                                                           .className("com.study.test.TestDao")
                                                                           .valueDependencies(
                                                                                   Map.of(KEY_FIELD, VALUE_FIELD))
                                                                           .build();

    public static final BeanDefinition SECOND_DAO_BEAN_DEFINITION = BeanDefinition.builder()
                                                                                  .id("second-dao")
                                                                                  .className("com.study.test.TestDao")
                                                                                  .valueDependencies(
                                                                                          Map.of(KEY_FIELD,
                                                                                                  VALUE_FIELD))
                                                                                  .build();

    public static final BeanDefinition SERVICE_BEAN_DEFINITION = BeanDefinition.builder()
                                                                               .id("service")
                                                                               .className("com.study.test.TestService")
                                                                               .refDependencies(Map.of(KEY_REF, "dao"))
                                                                               .build();

    public static final List<BeanDefinition> BEAN_DEFINITIONS = List.of(DAO_BEAN_DEFINITION, SERVICE_BEAN_DEFINITION);
    public static final List<BeanDefinition> ANOTHER_BEAN_DEFINITIONS =
            List.of(BeanDefinition.builder()
                                  .id("jdbc")
                                  .className("com.study.test.TestJdbc")
                                  .valueDependencies(
                                          Map.of("database", "h2"))
                                  .build(),
                    BeanDefinition.builder()
                                  .id("second-service")
                                  .className("com.study.test.TestSecondService")
                                  .refDependencies(Map.of("jdbc", "jdbc"))
                                  .build());
}
