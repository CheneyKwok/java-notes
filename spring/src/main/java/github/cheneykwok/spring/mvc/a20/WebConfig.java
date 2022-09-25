package github.cheneykwok.spring.mvc.a20;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;

@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
@EnableConfigurationProperties({ServerProperties.class, WebMvcProperties.class})
public class WebConfig {

    // 内嵌 web 容器工厂
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(ServerProperties serverProperties) {
        return new TomcatServletWebServerFactory(serverProperties.getPort());
    }

    // 创建 DispatcherServlet
    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    // 注册 DispatcherServlet, Spring MVC 的入口
    @Bean
    public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(DispatcherServlet dispatcherServlet,
                                                                               WebMvcProperties webMvcProperties) {
        DispatcherServletRegistrationBean registrationBean = new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        // 设置立即加载
        registrationBean.setLoadOnStartup(webMvcProperties.getServlet().getLoadOnStartup());
        return registrationBean;
    }

    // 如果用 DispatcherServlet 初始化时默认添加的组件, 并不会作为 bean, 给测试带来困扰
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {

        return new RequestMappingHandlerMapping();
    }

    @Bean
    public RequestMappingHandlerAdapter myRequestMappingHandlerAdapter() {
        MyRequestMappingHandlerAdapter handlerAdapter = new MyRequestMappingHandlerAdapter();
        handlerAdapter.setArgumentResolvers(Arrays.asList(new TokenArgumentResolver()));
        handlerAdapter.setReturnValueHandlers(Arrays.asList(new YmlMethodReturnResolver()));
        return handlerAdapter;
    }
}
