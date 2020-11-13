package no.nav.registre.testnorge.identservice.logging;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogExceptionsMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable { // NOSONAR
        try {
            return mi.proceed();
        } catch (Throwable e) { // NOSONAR - Thrown by interface
            LogExceptions logExceptions = findAnnotation(mi);
            logException(logExceptions, e);
            throw e;
        }
    }

    private LogExceptions findAnnotation(MethodInvocation mi) {
        return mi.getMethod().getAnnotation(LogExceptions.class);
    }

    private void logException(LogExceptions le, Throwable e) {
        if (arrayContainsExceptionType(le.functional(), e)) {
            log.error(e.getClass().getSimpleName() + " - " + e.getMessage()); // NOSONAR - Only need general info, not stacktrace
        } else if (!arrayContainsExceptionType(le.ignored(), e)) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean arrayContainsExceptionType(Class<?>[] classes, Throwable e) {
        for (Class<?> clazz : classes) {
            if (clazz.isAssignableFrom(e.getClass())) {
                return true;
            }
        }
        return false;
    }
}