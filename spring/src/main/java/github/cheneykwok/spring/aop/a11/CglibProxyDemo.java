package github.cheneykwok.spring.aop.a11;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxyDemo {

    static class Target  {
        public void foo() {
            System.out.println("target foo");
        }
    }

    public static void main(String[] args) {

        Target target = new Target();
        Target foo = (Target) Enhancer.create(Target.class, new MethodInterceptor() {

            @Override
            public Object intercept(Object p, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("before");
                // 内部使用反射
//                Object result = method.invoke(target, args);
                // 内部没有用到反射，需要目标（spring选择的方式）
                Object result = methodProxy.invoke(target, args);
                // 内部没有用到反射，需要代理
//                Object result = methodProxy.invokeSuper(p, args);
                System.out.println("after");
                return result;
            }
        });
        foo.foo();

    }
}
