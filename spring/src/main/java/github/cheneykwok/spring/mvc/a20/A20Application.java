package github.cheneykwok.spring.mvc.a20;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/*
          a. DispatcherServlet 是在第一次被访问时执行初始化, 也可以通过配置修改为 Tomcat 启动后就初始化
          b. 在初始化时会从 Spring 容器中找一些 Web 需要的组件, 如 HandlerMapping、HandlerAdapter 等

          两个重要的组件
          a. RequestMappingHandlerAdapter, 以 @RequestMapping 作为映射路径
          b. RequestMappingHandlerAdapter, 调用 handler
          c. 控制器的具体方法会被当作 handler
              - handler 的参数和返回值多种多样
              - 需要解析方法参数, 由 HandlerMethodArgumentResolver 来做
              - 需要处理方法返回值, 由 HandlerMethodReturnValueHandler 来做
      */
public class A20Application {

    public static void main(String[] args) throws Exception {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        // 作用：解析 RequestMapping 及其派生注解，生成路径与控制器方法的映射关系，在初始化时就生成
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        handlerMethods.forEach((k, v) -> {
            System.out.println(k + "=" + v);
        });

//        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/test3");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test4");
        request.addHeader("token", "某个令牌");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        // HandlerAdapter 作用: 调用控制器方法
        MyRequestMappingHandlerAdapter handlerAdapter = context.getBean(MyRequestMappingHandlerAdapter.class);
        handlerAdapter.invokeHandlerMethod(request, response, (HandlerMethod) chain.getHandler());

        // 检查响应
        byte[] content = response.getContentAsByteArray();
        System.out.println(new String(content, StandardCharsets.UTF_8));

        System.out.println("==========================================");
        // 所有的参数解析器
        for (HandlerMethodArgumentResolver argumentResolver : handlerAdapter.getArgumentResolvers()) {
            System.out.println(argumentResolver);
        }
//        org.springframework.web.method.annotation.RequestParamMethodArgumentResolver@7bb3a9fe
//        org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver@7cbee484
//        org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver@7f811d00
//        org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver@62923ee6
//        org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMethodArgumentResolver@4089713
//        org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMapMethodArgumentResolver@f19c9d2
//        org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@7807ac2c
//        org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor@b91d8c4
//        org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver@4b6166aa
//        org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver@a77614d
//        org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver@4fd4cae3
//        org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver@4a067c25
//        org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver@a1217f9
//        org.springframework.web.servlet.mvc.method.annotation.SessionAttributeMethodArgumentResolver@3bde62ff
//        org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver@523424b5
//        org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver@2baa8d82
//        org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver@319dead1
//        org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor@791cbf87
//        org.springframework.web.servlet.mvc.method.annotation.RedirectAttributesMethodArgumentResolver@a7e2d9d
//        org.springframework.web.method.annotation.ModelMethodProcessor@754777cd
//        org.springframework.web.method.annotation.MapMethodProcessor@2b52c0d6
//        org.springframework.web.method.annotation.ErrorsMethodArgumentResolver@372ea2bc
//        org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver@4cc76301
//        org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver@2f08c4b
//        org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver@3f19b8b3
//        org.springframework.web.method.annotation.RequestParamMethodArgumentResolver@7de0c6ae
//        org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@a486d78
        System.out.println("==========================================");
        // 所有的返回值解析器
        for (HandlerMethodReturnValueHandler returnValueHandler : handlerAdapter.getReturnValueHandlers()) {
            System.out.println(returnValueHandler);
        }
//        org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler@cdc3aae
//        org.springframework.web.method.annotation.ModelMethodProcessor@7ef2d7a6
//        org.springframework.web.servlet.mvc.method.annotation.ViewMethodReturnValueHandler@5dcbb60
//        org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler@4c36250e
//        org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBodyReturnValueHandler@21526f6c
//        org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor@49f5c307
//        org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler@299266e2
//        org.springframework.web.servlet.mvc.method.annotation.CallableMethodReturnValueHandler@5471388b
//        org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler@66ea1466
//        org.springframework.web.servlet.mvc.method.annotation.AsyncTaskMethodReturnValueHandler@1601e47
//        org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@3bffddff
//        org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor@66971f6b
//        org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler@50687efb
//        org.springframework.web.method.annotation.MapMethodProcessor@517bd097
//        org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@142eef62

    }
}
