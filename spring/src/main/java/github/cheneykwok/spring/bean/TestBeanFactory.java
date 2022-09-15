package github.cheneykwok.spring.bean;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Slf4j
public class TestBeanFactory {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config.class)
                .setScope("singleton")
                .getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);
        // 给 BeanFactory 注册一些常用处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);

        // 执行 BeanFactory 的后置处理器，主要用于 bean 的定义、生成
        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class)
                .values()
                .forEach(beanFactoryPostProcessor -> {
                    beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
                    log.debug("执行 BeanFactory postProcessor : {} ", beanFactoryPostProcessor);
                });

        // 执行 Bean 的后置处理器，主要用于 bean 的各项功能
        beanFactory.getBeansOfType(BeanPostProcessor.class)
                .values()
                .stream()
                 // Bean 的后置处理器有处理顺序
                .sorted(Objects.requireNonNull(beanFactory.getDependencyComparator()))
                .forEach(beanPostProcessor -> {
                    beanFactory.addBeanPostProcessor(beanPostProcessor);
                    log.debug("执行 Bean postProcessor : {} ", beanPostProcessor);
                });

        // 预先实例化单例对象
        beanFactory.preInstantiateSingletons();
        System.out.println("==================================");
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println(beanFactory.getBean(Bean1.class).bean2);
    }


    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    static class Bean1 {
        public Bean1() {
            log.debug("构造 Bean1()");
        }

        @Autowired
        @Getter
        private Bean2 bean2;
    }

    static class Bean2 {
        public Bean2() {
            log.debug("构造 Bean2()");
        }
    }
}
