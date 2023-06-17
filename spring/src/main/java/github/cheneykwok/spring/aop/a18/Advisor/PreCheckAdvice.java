package github.cheneykwok.spring.aop.a18.Advisor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * 自定义预检查 aop 通知
 *
 * @author gzc
 * @date 2023-06-17
 */
public class PreCheckAdvice implements MethodInterceptor, BeanFactoryAware {

    private final Pointcut pointCut;

    public PreCheckAdvice(Pointcut pointCut) {
        this.pointCut = pointCut;
    }

    private final StandardEvaluationContext context = new StandardEvaluationContext();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));

    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        ReflectiveMethodInvocation reflectiveMethodInvocation = (ReflectiveMethodInvocation) invocation;
        Class<?> targetClass = reflectiveMethodInvocation.getThis().getClass();
        PreCheckPointCut preCheckPointCut = (PreCheckPointCut) pointCut;
        Expression expression = preCheckPointCut.getCachedExpression(method, targetClass);
        Boolean res = expression.getValue(context, Boolean.class);
        return invocation.proceed();
    }



}
