package github.cheneykwok.spring.bean.a05;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ConfigurationClassBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        try {
            ClassPathResource resource = new ClassPathResource("github/cheneykwok/spring/bean/a05/Config.class");
            MetadataReader metadataReader = new CachingMetadataReaderFactory().getMetadataReader(resource);
            Set<MethodMetadata> methods = metadataReader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
            for (MethodMetadata method : methods) {
                Map<String, Object> annotationAttributes = method.getAnnotationAttributes(Bean.class.getName());
                String initMethod = annotationAttributes.get("initMethod").toString();
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                        .genericBeanDefinition()
                        .setFactoryMethodOnBean(method.getMethodName(), "config")
                        .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
                if (!initMethod.isEmpty()) {
                    beanDefinitionBuilder.setInitMethodName(initMethod);
                }
                registry.registerBeanDefinition(method.getMethodName(), beanDefinitionBuilder.getBeanDefinition());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
