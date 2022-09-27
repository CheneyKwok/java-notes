package github.cheneykwok.spring.web.a24;

import github.cheneykwok.spring.web.a23.MyDateFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@Configuration
public class WebConfig {

    @ControllerAdvice // 控制器增强类 三个功能：@ExceptionHandler、@InitBinder、@ModelAttribute
    static class MyControllerAdvice {
        @InitBinder
        public void binder3(WebDataBinder webDataBinder) {
            webDataBinder.addCustomFormatter(new MyDateFormatter("binder3 转换器"));
        }
    }

    @Controller
    static class Controller1 {
        @InitBinder
        public void binder1(WebDataBinder webDataBinder) {
            webDataBinder.addCustomFormatter(new MyDateFormatter("binder1 转换器"));
        }

        public void foo() {

        }
    }

    @Controller
    static class Controller2 {
        @InitBinder
        public void binder21(WebDataBinder webDataBinder) {
            webDataBinder.addCustomFormatter(new MyDateFormatter("binder21 转换器"));
        }

        @InitBinder
        public void binder22(WebDataBinder webDataBinder) {
            webDataBinder.addCustomFormatter(new MyDateFormatter("binder22 转换器"));
        }

        public void bar() {

        }
    }

}
