package no.nav.dolly.config;

import ch.qos.logback.classic.LoggerContext;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Lagt til som en workaround for at {@code LogbackLoggingSystem#stopAndReset(...)} gjør en {@code LoggerContext#stop()} som fjerner {@code LogbackLoggingSystem} fra {@code LoggerContext#getTurboFilterList()}.
 * <br/><br/>
 * Dermed mister man metrics av typen {@code logback_events_total}.
 * <br/><br/>
 * Dette er sannsynligvis en bug i samhandlingen mellom diverse komponenter i Spring Boot og Micrometer, ref. Google. Hvis samme sees for andre applikasjoner så bør kanskje dette flyttes til felles lib.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class LogbackMetricsWorkaroundConfig {

    private final MeterRegistry registry;

    @PostConstruct
    public void postConstruct() {

        var loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext
                .getTurboFilterList()
                .stream()
                .map(filter -> filter.getClass().getSimpleName())
                .filter(simpleClassName -> simpleClassName.equals("MetricsTurboFilter")) // MetricsTurboFilter isn't public.
                .findFirst()
                .ifPresentOrElse(
                        simpleClassName -> log.warn("{} found in existing turboFilterList() - consider scrapping {}", simpleClassName, LogbackMetricsWorkaroundConfig.class.getSimpleName()),
                        () -> {
                            try (var logbackMetrics = new LogbackMetrics()) {
                                logbackMetrics.bindTo(registry);
                            }
                        }
                );

    }

}
