package com.study;

import com.study.reader.BeanDefinitionReader;
import com.study.test.TestDao;
import com.study.test.TestService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.study.TestData.BEAN_DEFINITIONS;
import static com.study.TestData.DAO_BEAN_DEFINITION;
import static com.study.TestData.KEY_FIELD;
import static com.study.TestData.SECOND_DAO_BEAN_DEFINITION;
import static com.study.TestData.SERVICE_BEAN_DEFINITION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ClassPathApplicationContextTest {

    private final BeanDefinitionReader beanDefinitionReader = mock(BeanDefinitionReader.class);
    private ApplicationContext applicationContext;


    @Test
    void should_returnBean_whenCreateBeansAndGetById() {
        when(beanDefinitionReader.readBeanDefinitions()).thenReturn(BEAN_DEFINITIONS);
        applicationContext = new ClassPathApplicationContext(beanDefinitionReader);

        var daoBean = (TestDao) applicationContext.getBean(DAO_BEAN_DEFINITION.getId());
        var serviceBean = (TestService) applicationContext.getBean(SERVICE_BEAN_DEFINITION.getId());
        assertEquals(DAO_BEAN_DEFINITION.getClassName(), daoBean.getClass()
                                                                .getCanonicalName());
        assertEquals(SERVICE_BEAN_DEFINITION.getClassName(), serviceBean.getClass()
                                                                        .getCanonicalName());
        assertEquals(DAO_BEAN_DEFINITION.getValueDependencies()
                                        .get(KEY_FIELD), daoBean.getDatabase());
        assertEquals(daoBean, serviceBean.getDao());
        verify(beanDefinitionReader).readBeanDefinitions();
        verifyNoMoreInteractions(beanDefinitionReader);
    }

    @Test
    void should_returnBean_whenCreateBeansAndGetByIdWithClass() {
        when(beanDefinitionReader.readBeanDefinitions()).thenReturn(BEAN_DEFINITIONS);
        applicationContext = new ClassPathApplicationContext(beanDefinitionReader);

        var daoBean = applicationContext.getBean(DAO_BEAN_DEFINITION.getId(), TestDao.class);
        var serviceBean = (TestService) applicationContext.getBean(SERVICE_BEAN_DEFINITION.getId(), TestService.class);
        assertEquals(DAO_BEAN_DEFINITION.getClassName(), daoBean.getClass()
                                                                .getCanonicalName());
        assertEquals(SERVICE_BEAN_DEFINITION.getClassName(), serviceBean.getClass()
                                                                        .getCanonicalName());
        assertEquals(DAO_BEAN_DEFINITION.getValueDependencies()
                                        .get(KEY_FIELD), daoBean.getDatabase());
        assertEquals(daoBean, serviceBean.getDao());
        verify(beanDefinitionReader).readBeanDefinitions();
        verifyNoMoreInteractions(beanDefinitionReader);
    }

    @Test
    void should_returnBean_whenCreateBeansAndGetByClass() {
        when(beanDefinitionReader.readBeanDefinitions()).thenReturn(BEAN_DEFINITIONS);
        applicationContext = new ClassPathApplicationContext(beanDefinitionReader);

        var daoBean = applicationContext.getBean(TestDao.class);
        var serviceBean = applicationContext.getBean(TestService.class);
        assertEquals(DAO_BEAN_DEFINITION.getClassName(), daoBean.getClass()
                                                                .getCanonicalName());
        assertEquals(SERVICE_BEAN_DEFINITION.getClassName(), serviceBean.getClass()
                                                                        .getCanonicalName());
        assertEquals(DAO_BEAN_DEFINITION.getValueDependencies()
                                        .get(KEY_FIELD), daoBean.getDatabase());
        assertEquals(daoBean, serviceBean.getDao());
        verify(beanDefinitionReader).readBeanDefinitions();
        verifyNoMoreInteractions(beanDefinitionReader);
    }

    @Test
    void should_throwIllegalStateException_whenCreateBeansWithDuplicateId() {
        when(beanDefinitionReader.readBeanDefinitions()).thenReturn(List.of(DAO_BEAN_DEFINITION, DAO_BEAN_DEFINITION));
        assertThrows(IllegalStateException.class, () -> new ClassPathApplicationContext(beanDefinitionReader));
    }

    @Test
    void should_IllegalStateException_whenCreateBeansDuplicateByClassAndGetByClass() {
        when(beanDefinitionReader.readBeanDefinitions()).thenReturn(
                List.of(DAO_BEAN_DEFINITION, SECOND_DAO_BEAN_DEFINITION));
        applicationContext = new ClassPathApplicationContext(beanDefinitionReader);
        assertThrows(IllegalStateException.class, () -> applicationContext.getBean(TestDao.class));
    }
}