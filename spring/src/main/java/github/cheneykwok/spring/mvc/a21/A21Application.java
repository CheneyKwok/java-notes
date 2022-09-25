package github.cheneykwok.spring.mvc.a21;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 参数处理器
 * org.springframework.web.method.annotation.RequestParamMethodArgumentResolver@7bb3a9fe
 * org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver@7cbee484
 * org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver@7f811d00
 * org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver@62923ee6
 * org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMethodArgumentResolver@4089713
 * org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMapMethodArgumentResolver@f19c9d2
 * org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@7807ac2c
 * org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor@b91d8c4
 * org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver@4b6166aa
 * org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver@a77614d
 * org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver@4fd4cae3
 * org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver@4a067c25
 * org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver@a1217f9
 * org.springframework.web.servlet.mvc.method.annotation.SessionAttributeMethodArgumentResolver@3bde62ff
 * org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver@523424b5
 * org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver@2baa8d82
 * org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver@319dead1
 * org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor@791cbf87
 * org.springframework.web.servlet.mvc.method.annotation.RedirectAttributesMethodArgumentResolver@a7e2d9d
 * org.springframework.web.method.annotation.ModelMethodProcessor@754777cd
 * org.springframework.web.method.annotation.MapMethodProcessor@2b52c0d6
 * org.springframework.web.method.annotation.ErrorsMethodArgumentResolver@372ea2bc
 * org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver@4cc76301
 * org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver@2f08c4b
 * org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver@3f19b8b3
 * org.springframework.web.method.annotation.RequestParamMethodArgumentResolver@7de0c6ae
 * org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@a486d78
 */
public class A21Application {

    public static void main(String[] args) throws Exception {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.refresh();
        HttpServletRequest request = mockRequest();
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        // 要点1. 控制器方法被封装为 HandlerMethod
        HandlerMethod method = new HandlerMethod(new Controller(), Controller.class.getMethod("test", String.class, String.class, int.class, String.class, MultipartFile.class, int.class, String.class, String.class, String.class, HttpServletRequest.class, User.class, User.class, User.class));
        // 要点2. 准备对象绑定与类型转换
        ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(null, null);
        // 要点3. 准备 ModelAndViewContainer 用来存储中间 Model 结果
        ModelAndViewContainer container = new ModelAndViewContainer();
        // 要点4. 解析每个参数值
        for (MethodParameter parameter : method.getMethodParameters()) {
            // 设置参数名称解析器（反射 + 本地变量表）
            parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());

            // 解析器组合
            HandlerMethodArgumentResolverComposite resolverComposite = new HandlerMethodArgumentResolverComposite();
            resolverComposite.addResolvers(
                    // beanFactory 提供解析环境变量功能
                    new RequestParamMethodArgumentResolver(beanFactory, false),// 需要 @RequestParam
                    new PathVariableMethodArgumentResolver(),
                    new RequestHeaderMethodArgumentResolver(beanFactory),
                    new ServletCookieValueMethodArgumentResolver(beanFactory),
                    new ExpressionValueMethodArgumentResolver(beanFactory),
                    new ServletRequestMethodArgumentResolver(),
                    new ServletModelAttributeMethodProcessor(false),// 需要 @ModelAttribute
                    new RequestResponseBodyMethodProcessor(Arrays.asList(new MappingJackson2HttpMessageConverter())),
                    new ServletModelAttributeMethodProcessor(true),// 不需要 @ModelAttribute
                    new RequestParamMethodArgumentResolver(beanFactory, true)
            );

            String annotations = Arrays.stream(parameter.getParameterAnnotations()).map(a -> a.annotationType().getSimpleName()).collect(Collectors.joining());
            String str = annotations.length() > 0 ? " @" + annotations + " " : " ";
            if (resolverComposite.supportsParameter(parameter)) {
                Object v = resolverComposite.resolveArgument(parameter, container, new ServletWebRequest(request), factory);
                System.out.println("[" + parameter.getParameterIndex() + "] " + str + parameter.getParameterType().getSimpleName() + " " + parameter.getParameterName() + "->" + v);
                System.out.println("模型数据为：" + container.getModel());
            } else {
                System.out.println("[" + parameter.getParameterIndex() + "] " + str + parameter.getParameterType().getSimpleName() + " " + parameter.getParameterName());
            }

        }

    }

    private static HttpServletRequest mockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("name1", "zhangsan");
        request.setParameter("name2", "lisi");
        request.addPart(new MockPart("file", "abc", "hello".getBytes(StandardCharsets.UTF_8)));
        Map<String, String> map = new AntPathMatcher().extractUriTemplateVariables("/test/{id}", "/test/123");
        System.out.println(map);
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, map);
        request.setContentType("application/json");
        request.setCookies(new Cookie("token", "123456"));
        request.setParameter("name", "张三");
        request.setParameter("age", "18");
        request.setContent("{\"name\":\"张三\",\"age\":10}".getBytes(StandardCharsets.UTF_8));

        return new StandardServletMultipartResolver().resolveMultipart(request);
    }

    static class Controller {
        public void test(
                @RequestParam("name1") String name1, // name1=张三
                String name2,                        // name2=李四
                @RequestParam("age") int age,        // age=18
                @RequestParam(name = "home", defaultValue = "${JAVA_HOME}") String home1, // spring 获取数据
                @RequestParam("file") MultipartFile file, // 上传文件
                @PathVariable("id") int id,               //  /test/124   /test/{id}
                @RequestHeader("Content-Type") String header,
                @CookieValue("token") String token,
                @Value("${JAVA_HOME}") String home2, // spring 获取数据  ${} #{}
                HttpServletRequest request,          // request, response, session ...
                @ModelAttribute("abc") User user1,          // name=zhang&age=18
                User user2,                          // name=zhang&age=18
                @RequestBody User user3              // json
        ) {
        }
    }

    static class User {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
