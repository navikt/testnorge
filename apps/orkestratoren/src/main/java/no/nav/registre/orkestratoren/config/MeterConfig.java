package no.nav.registre.orkestratoren.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import no.nav.registre.orkestratoren.meter.WebClientTagsProvider;
import org.springframework.boot.actuate.metrics.AutoTimer;
import org.springframework.boot.actuate.metrics.web.reactive.client.MetricsWebClientFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

@Configuration
@RequiredArgsConstructor
public class MeterConfig {

    private final MeterRegistry meterRegistry;

    @Bean
    public ExchangeFilterFunction metricsWebClientFilterFunction() {
        return new MetricsWebClientFilterFunction(meterRegistry, new WebClientTagsProvider(),
                "webClientMetrics", AutoTimer.ENABLED);
    }
}