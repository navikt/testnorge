package no.nav.dolly.config;

import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.reactive.ServerHttpObservationFilter;

@Configuration
@RequiredArgsConstructor
public class MeterConfig {

    private final ObservationRegistry observationRegistry;

    @Bean
    public ServerHttpObservationFilter metricsWebClientFilterFunction() {
        return new ServerHttpObservationFilter(observationRegistry);
    }
}