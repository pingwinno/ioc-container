package com.study.reader;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.study.TestData.ANOTHER_BEAN_DEFINITIONS;
import static com.study.TestData.BEAN_DEFINITIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class XmlBeanDefinitionReaderITest {

    private final static String CONFIG = XmlBeanDefinitionReaderITest.class
            .getClassLoader()
            .getResource("context.xml")
            .getFile();

    private final static String ANOTHER_CONFIG = XmlBeanDefinitionReaderITest.class
            .getClassLoader()
            .getResource("another-context.xml")
            .getFile();

    private final static String CONFIG_WITH_ID_DUPLICATE = XmlBeanDefinitionReaderITest.class
            .getClassLoader()
            .getResource("context-with-id-duplicate.xml")
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