package github.cheneykwok.spring.aop.a18.Advisor;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gzc
 * @date 2023-06-17
 */
@Configuration
public class AopConfig {


    /**
     * 自定义预检查 aop 切面
     *
     * @return Advisor
     * @author gzc
     * @date 2023-06-17
     */
    @Bean
    public Advisor perCheckAdvisor(MethodInterceptor perCheckAdvice, Pointcut preCheckPointCut) {

        return new DefaultPointcutAdvisor(preCheckPointCut, perCheckAdvice);

    }

    @Bean
    public Pointcut preCheckPointCut() {
        PreCheckPointCut pointCut = new PreCheckPointCut();
        return pointCut;
    }


    @Bean
    public MethodInterceptor perCheckAdvice(Pointcut preCheckPointCut) {
        return new PreCheckAdvice(preCheckPointCut);
    }
}
