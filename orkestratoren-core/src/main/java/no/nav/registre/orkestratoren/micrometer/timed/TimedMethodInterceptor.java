package no.nav.registre.orkestratoren.micrometer.timed;

import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimedMethodInterceptor implements MethodInterceptor {
    private final MeterRegistry registry;

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        boolean success = true;
        Timer.Sample sample = Timer.start(registry);
        try {
            return mi.proceed();
        } catch (Throwable e) {
            success = false;
            throw e;
        } finally {
            Timed timed = findAnnotation(mi);
            sample.stop(registry.timer(timed.name(), addAdditionalTags(timed.tags(), success)));
        }
    }

    private String[] addAdditionalTags(String[] suppliedTags, boolean success) {
        String[] finalTags = Arrays.copyOf(suppliedTags, suppliedTags.length + 6);

        finalTags[finalTags.length - 6] = "consumerId";
        finalTags[finalTags.length - 5] = MDC.get("consumer-id") == null ? "internal" : MDC.get("consumer-id");

        finalTags[finalTags.length - 4] = "request.type";
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        finalTags[finalTags.length - 3] = requestAttributes == null ? "internal" : ((String) requestAttributes.getRequest().getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).replaceAll("/api/", "");

        finalTags[finalTags.length - 2] = "status";
        finalTags[finalTags.length - 1] = success ? "success" : "fail";

        return finalTags;
    }

    private Timed findAnnotation(MethodInvocation mi) {
        return mi.getMethod().getAnnotation(Timed.class);
    }
}
