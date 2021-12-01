package com.study.reader;

import com.study.model.BeanDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlBeanDefinitionReaderITest {

    private static final List<BeanDefinition> BEAN_DEFINITIONS =
            List.of(BeanDefinition.builder()
                                  .id("dao")
                                  .className("com.study.test.TestDao")
                                  .valueDependencies(
                                          Map.of("database", "h2"))
                                  .build(),
                    BeanDefinition.builder()
                                  .id("service")
                                  .className("com.study.test.TestService")
                                  .valueDependencies(Map.of("dao", "dao"))
                                  .build());
    private static final List<BeanDefinition> ANOTHER_BEAN_DEFINITIONS =
            List.of(BeanDefinition.builder()
                                  .id("jdbc")
                                  .className("com.study.test.TestJdbc")
                                  .valueDependencies(
                                          Map.of("database", "h2"))
                                  .build(),
                    BeanDefinition.builder()
                                  .id("second-service")
                                  .className("com.study.test.TestSecondService")
                                  .valueDependencies(Map.of("jdbc", "jdbc"))
                                  .build());

    private final static String CONFIG = XmlBeanDefinitionReaderITest.class
            .getClassLoader()
            .getResource("context.xml")
            .getFile();

    private final static String ANOTHER_CONFIG = XmlBeanDefinitionReaderITest.class
            .getClassLoader()
            .getResource("another-context.xml")
            .getFile();


    @Test
    void should_returnBeanDefinitions_when_parseContext() {
        var beanDefinitionReader = new XmlBeanDefinitionReader();
        var actualBeanDefinitions = beanDefinitionReader.readBeanDefinitions();
        assertEquals(BEAN_DEFINITIONS, actualBeanDefinitions);
    }

    @Test
    void should_returnBeanDefinitions_when_parseCustomContext() {
        var beanDefinitionReader = new XmlBeanDefinitionReader(ANOTHER_CONFIG);
        var actualBeanDefinitions = beanDefinitionReader.readBeanDefinitions();
        assertEquals(ANOTHER_BEAN_DEFINITIONS, actualBeanDefinitions);
    }

    @Test
    void should_returnBeanDefinitions_when_parseTwoContext() {
        var beanDefinitionReader = new XmlBeanDefinitionReader(CONFIG, ANOTHER_CONFIG);
        var actualBeanDefinitions = beanDefinitionReader.readBeanDefinitions();
        var expectedBeanDefinitions = Stream.concat(BEAN_DEFINITIONS.stream(), ANOTHER_BEAN_DEFINITIONS.stream())
                                            .collect(Collectors.toList());
        assertEquals(expectedBeanDefinitions, actualBeanDefinitions);
    }

}