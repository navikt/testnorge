package no.nav.dolly.metrics;

import java.util.Arrays;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimedMethodInterceptor implements MethodInterceptor {
    private final MeterRegistry registry;

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Timer.Sample sample = Timer.start(registry);

        boolean success = true;
        try {
            return mi.proceed();
        } catch (Throwable e) {
            success = false;
            throw e;
        } finally {
            Timed timed = findAnnotation(mi);
            sample.stop(Timer.builder(timed.name())
                    .tags(addAdditionalTags(timed.tags(), success))
                    .publishPercentileHistogram()
                    .register(registry));
        }
    }

    private String[] addAdditionalTags(String[] suppliedTags, boolean success) {
        String[] finalTags = Arrays.copyOf(suppliedTags, suppliedTags.length + 2);

        finalTags[finalTags.length - 2] = "status";
        finalTags[finalTags.length - 1] = success ? "success" : "failure";

        return finalTags;
    }

    private Timed findAnnotation(MethodInvocation mi) {
        return mi.getMethod().getAnnotation(Timed.class);
    }
}
