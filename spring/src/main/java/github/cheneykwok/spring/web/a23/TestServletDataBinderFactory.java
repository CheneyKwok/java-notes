package github.cheneykwok.spring.web.a23;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

import java.util.Collections;
import java.util.Date;

public class TestServletDataBinderFactory {

    public static void main(String[] args) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("birthday", "1999|01|01");
        request.setParameter("address.name", "北京");
        User user = new User();
        // 1. 用工厂 未扩展转换功能
//        ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(null, null);
//        WebDataBinder dataBinder = factory.createBinder(new ServletWebRequest(request), user, "user");
        // 2. 添加 @InitBinder 转换
        InvocableHandlerMethod method = new InvocableHandlerMethod(new MyController(), MyController.class.getMethod("a", WebDataBinder.class));
//        ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(Collections.singletonList(method), null);
        // 3. 添加 ConversionService 转换
        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addFormatter(new MyDateFormatter("用 ConversionService 方式扩展"));
        ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
        initializer.setConversionService(conversionService);
//        ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(null, initializer);
        // 4. 同时添加 @InitBinder、ConversionService，@InitBinder优先级更高
//        ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(Collections.singletonList(method), initializer);
        // 5. 使用 FormattingConversionService 默认实现 配合 @DateTimeFormat
        conversionService = new DefaultFormattingConversionService();
        initializer.setConversionService(conversionService);
        ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(null, initializer);
        WebDataBinder dataBinder = factory.createBinder(new ServletWebRequest(request), user, "user");
        dataBinder.bind(new ServletRequestParameterPropertyValues(request));
        System.out.println(user);

    }

    // 控制器类
    static class MyController {
        @InitBinder
        public void a(WebDataBinder dataBinder) {
            // 扩展 dataBinder 的转换器
            dataBinder.addCustomFormatter(new MyDateFormatter("用 @InitBinder 方式扩展的"));

        }
    }

    public static class User {
        @DateTimeFormat(pattern = "yyyy|MM|dd")
        private Date birthday;
        private Address address;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        @Override
        public String toString() {
            return "User{" +
                    "birthday=" + birthday +
                    ", address=" + address +
                    '}';
        }
    }

    public static class Address {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
