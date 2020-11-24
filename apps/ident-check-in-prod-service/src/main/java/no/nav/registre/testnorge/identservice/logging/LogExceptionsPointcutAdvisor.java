package no.nav.registre.testnorge.identservice.logging;

import lombok.RequiredArgsConstructor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

@Configuration
@RequiredArgsConstructor
public class LogExceptionsPointcutAdvisor extends AbstractPointcutAdvisor {

    private static final StaticMethodMatcherPointcut POINTCUT = new StaticMethodMatcherPointcut() {
        @Override
        public boolean matches(Method method, Class<?> aClass) {
            return method.isAnnotationPresent(LogExceptions.class);
        }
    };

    private final LogExceptionsMethodInterceptor interceptorAdvice;

    @Override
    public Pointcut getPointcut() {
        return POINTCUT;
    }

    @Override
    public Advice getAdvice() {
        return interceptorAdvice;
    }
}