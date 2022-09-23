package github.cheneykwok.spring.aop.a18;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyMethodInvocation implements ProxyMethodInvocation, Cloneable {


    private final Object proxy;
    private final Object target;

    private final Method method;

    private Object[] arguments;

    private final List<Object> interceptors;

    private int interceptorsIndex = -1;

    private Map<String, Object> userAttributes;

    public MyMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, List<Object> interceptors) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.interceptors = interceptors;
    }


    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Object proceed() throws Throwable {
        if (interceptorsIndex == interceptors.size() - 1) {
            // 调用目标， 返回并结束递归
            return method.invoke(target, arguments);
        }
        // 逐一调用通知
        MethodInterceptor interceptor = (MethodInterceptor) interceptors.get(++interceptorsIndex);
        return interceptor.invoke(this);
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return method;
    }

    @Override
    public Object getProxy() {
        return proxy;
    }

    @Override
    public MethodInvocation invocableClone() {
        Object[] cloneArguments = this.arguments;
        if (this.arguments.length > 0) {
            // Build an independent copy of the arguments array.
            cloneArguments = this.arguments.clone();
        }
        return invocableClone(cloneArguments);
    }

    @Override
    public MethodInvocation invocableClone(Object... arguments) {
        if (this.userAttributes == null) {
            this.userAttributes = new HashMap<>();
        }

        // Create the MethodInvocation clone.
        try {
            MyMethodInvocation clone = (MyMethodInvocation) clone();
            clone.arguments = arguments;
            return clone;
        }
        catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(
                    "Should be able to clone object of type [" + getClass() + "]: " + ex);
        }

    }

    @Override
    public void setArguments(Object... arguments) {
        this.arguments = arguments;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<>();
            }
            this.userAttributes.put(key, value);
        }
        else {
            if (this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }
}
