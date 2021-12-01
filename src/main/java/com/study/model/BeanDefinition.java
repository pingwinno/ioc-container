package com.study.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class BeanDefinition {
    private String id;
    private String className;
    @Builder.Default
    private Map<String, String> valueDependencies = Collections.EMPTY_MAP;
    @Builder.Default
    private Map<String, String> refDependencies = Collections.EMPTY_MAP;
}
