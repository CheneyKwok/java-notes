package github.cheneykwok.spring.aop.a18;

import github.cheneykwok.spring.aop.a18.Advisor.PreCheck;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AspectJToAdvisor {

    static class AspectJ {

        @Before("execution(* foo())")
        public void before1() {
            System.out.println("before1");
        }

        @Before("execution(* foo())")
        public void before2() {
            System.out.println("before2");
        }

        @After("execution(* foo())")
        public void after() {
            System.out.println("after");
        }

        @AfterReturning("execution(* foo())")
        public void AfterReturning() {
            System.out.println("AfterReturning");
        }

        @AfterThrowing("execution(* foo())")
        public void AfterThrowing(Exception e) {
            System.out.println("AfterThrowing " + e.getMessage());
        }

        @Around("execution(* foo())")
        public Object Around(ProceedingJoinPoint joinPoint) {
            try {
                System.out.println("around before");
                return joinPoint.proceed();
            } catch (Throwable e) {
                return null;
            } finally {
                System.out.println("around after");
            }
        }
    }

    static class Target {

        @PreCheck("#a == 1")
        public void foo(Integer a) {
            System.out.println("target foo");
        }
    }

    public static void main(String[] args) throws Throwable {

        AspectInstanceFactory factory = new SingletonAspectInstanceFactory(new AspectJ());
        List<Advisor> advisorList = new ArrayList<>();
        Method[] methods = AspectJ.class.getMethods();
        // 收集所有 Aspect 高级切面并转换为低级切面 advisor
        for (Method method : methods) {
            if (method.isAnnotationPresent(Before.class)) {
                // 解析切点
                String expression = method.getAnnotation(Before.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 构建通知
                AspectJMethodBeforeAdvice advice = new AspectJMethodBeforeAdvice(method, pointcut, factory);
                // 构建切面
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisorList.add(advisor);
            } else if (method.isAnnotationPresent(AfterReturning.class)) {
                String expression = method.getAnnotation(AfterReturning.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                AspectJAfterReturningAdvice advice = new AspectJAfterReturningAdvice(method, pointcut, factory);
                advisorList.add(new DefaultPointcutAdvisor(pointcut, advice));
            } else if (method.isAnnotationPresent(Around.class)) {
                String expression = method.getAnnotation(Around.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                AspectJAroundAdvice advice = new AspectJAroundAdvice(method, pointcut, factory);
                advisorList.add(new DefaultPointcutAdvisor(pointcut, advice));
            }

        }
        advisorList.forEach(System.out::println);
        System.out.println("===========================");

        Target target = new Target();
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.setTargetClass(target.getClass());
        // 为当前线程保存公开一个 MethodInvocation
        proxyFactory.addAdvice(ExposeInvocationInterceptor.INSTANCE);
        proxyFactory.addAdvisors(advisorList);

        // 通知统一适配为环绕通知 MethodInterceptor
        Method method = Target.class.getMethod("foo");
        List<Object> list = proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(method, target.getClass());
        list.forEach(System.out::println);
        System.out.println("===========================");

        //  创建并执行调用链 (执行所有环绕通知 + 目标)
        MyMethodInvocation invocation = new MyMethodInvocation(null, target, method, new Object[0], list);
        invocation.proceed();


    }

}
