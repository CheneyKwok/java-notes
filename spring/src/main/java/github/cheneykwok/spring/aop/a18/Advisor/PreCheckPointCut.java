package github.cheneykwok.spring.aop.a18.Advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义预检查 aop 切点
 *
 * @author gzc
 * @date 2023-06-17
 */
@Slf4j
public class PreCheckPointCut implements Pointcut {

    private final Map<DefaultCacheKey, Expression> preCheckExpressions = new HashMap<>();

    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public ClassFilter getClassFilter() {
        return (clazz -> true);
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                Expression cached = getCachedExpression(method, targetClass);
                return cached != null;
            }

            @Override
            public boolean isRuntime() {
                return false;
            }

            @Override
            public boolean matches(Method method, Class<?> targetClass, Object... args) {
                return false;
            }
        };
    }


    public Expression getCachedExpression(Method method, Class<?> targetClass) {
        DefaultCacheKey cacheKey = new DefaultCacheKey(method, targetClass);
        Expression expression = preCheckExpressions.get(cacheKey);
        if (expression != null) {
            return expression;
        }
        PreCheck annotation = AnnotationUtils.findAnnotation(method, PreCheck.class);
        if (annotation != null) {
            String value = annotation.value();
            expression = parser.parseExpression(value);
            preCheckExpressions.put(cacheKey, expression);
            log.info("add cached SpEL, method ({}), expression ({})", cacheKey, expression);
        }

        return expression;
    }

    private static class DefaultCacheKey {
        private final Method method;
        private final Class<?> targetClass;

        public DefaultCacheKey(Method method, Class<?> targetClass) {
            this.method = method;
            this.targetClass = targetClass;
        }

        @Override
        public boolean equals(Object other) {
            DefaultCacheKey otherKey = (DefaultCacheKey) other;
            return (this.method.equals(otherKey.method) && ObjectUtils.nullSafeEquals(
                    this.targetClass, otherKey.targetClass));
        }

        @Override
        public int hashCode() {
            return this.method.hashCode() * 21
                    + (this.targetClass != null ? this.targetClass.hashCode() : 0);
        }

        @Override
        public String toString() {
            return "CacheKey[" + (targetClass == null ? "-" : targetClass.getName())
                    + "; " + method + "]";
        }
    }
}
