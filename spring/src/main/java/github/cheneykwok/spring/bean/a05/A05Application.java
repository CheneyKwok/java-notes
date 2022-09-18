package github.cheneykwok.spring.bean.a05;

import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

public class A05Application {

    public static void main(String[] args) {

        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("config", Config.class);
        // @PropertySource @ComponentScan @Bean @Import @ImportResource
//        context.registerBean(ConfigurationClassPostProcessor.class);
        // 自定义
        context.registerBean(ComponentScanPostProcessor.class); // @ComponentScan
        context.registerBean(ConfigurationClassBeanPostProcessor.class); // @Bean
        context.registerBean(MapperPostProcessor.class); // @Mapper
        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }

    }
}
