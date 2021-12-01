package com.study.reader;

import com.study.model.BeanDefinition;
import lombok.SneakyThrows;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class XmlBeanDefinitionReader implements BeanDefinitionReader {

    private static final String DEFAULT_CONFIG = XmlBeanDefinitionReader.class
            .getClassLoader()
            .getResource("context.xml")
            .getFile();

    private static final String REF = "ref";
    private static final String VALUE = "value";
    private static final String NAME = "name";
    private static final String BEAN = "bean";

    private final List<String> configs;
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();


    public XmlBeanDefinitionReader() {
        this(DEFAULT_CONFIG);
    }

    public XmlBeanDefinitionReader(String... configs) {
        this.configs = List.of(configs);
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {
        return configs.parallelStream()
                      .flatMap(config -> readConfig(config).stream())
                      .collect(Collectors.toList());
    }


    @SneakyThrows
    private List<BeanDefinition> readConfig(String config) {
        var builder = factory.newDocumentBuilder();
        var beanDefinitionList = new ArrayList<BeanDefinition>();
        var document = builder.parse(new File(config));
        document.getDocumentElement()
                .normalize();
        var elements = document.getElementsByTagName(BEAN);
        for (int i = 0; i < elements.getLength(); i++) {
            var bean = elements.item(i);
            NamedNodeMap nodeMap = bean.getAttributes();
            var properties = bean.getChildNodes();

            var refs = new HashMap<String, String>();
            var values = new HashMap<String, String>();
            for (int j = 0; j < properties.getLength(); j++) {
                var propertyAttributes = properties.item(j)
                                                   .getAttributes();
                if (propertyAttributes != null) {
                    var ref = propertyAttributes.getNamedItem(REF);
                    var name = propertyAttributes.getNamedItem(NAME);
                    var value = propertyAttributes.getNamedItem(VALUE);
                    if (ref != null) {
                        values.put(name.getNodeValue(), ref.getNodeValue());
                    } else if (value != null) {
                        values.put(name.getNodeValue(), value.getNodeValue());
                    }
                }
            }
            beanDefinitionList.add(BeanDefinition.builder()
                                                 .id(nodeMap.getNamedItem("id")
                                                            .getNodeValue())
                                                 .className(nodeMap.getNamedItem("class")
                                                                   .getNodeValue())
                                                 .valueDependencies(values)
                                                 .refDependencies(refs)
                                                 .build());
        }
        return beanDefinitionList;
    }
}
