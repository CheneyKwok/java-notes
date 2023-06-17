package github.cheneykwok.spring.aop.a18.Advisor;

import java.lang.annotation.*;


/**
 * 预检查注解
 *
 * @author gzc
 * @date 2023-06-17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PreCheck {

    String value();
}
