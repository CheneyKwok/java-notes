package github.cheneykwok.spring.aop.a11;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkProxyDemo {

    interface Foo {
        void foo();
    }

    static class Target implements Foo {
        @Override
        public void foo() {
            System.out.println("target foo");
        }
    }

    public static void main(String[] args) {

        Target target = new Target();
        ClassLoader loader = JdkProxyDemo.class.getClassLoader();
        Foo foo = (Foo) Proxy.newProxyInstance(loader, new Class[]{Foo.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(proxy.getClass());
                System.out.println("before...");
                Object invoke = method.invoke(target, args);
                System.out.println("after...");
                return invoke;
            }
        });
        foo.foo();
        System.out.println(foo.getClass());

    }
}
