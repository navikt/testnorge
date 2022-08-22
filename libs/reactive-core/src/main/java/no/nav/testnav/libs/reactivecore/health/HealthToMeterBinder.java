package no.nav.testnav.libs.reactivecore.health;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.util.function.ToDoubleFunction;

@RequiredArgsConstructor
public class HealthToMeterBinder implements MeterBinder {

    private final HealthContributorRegistry registry;

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        registry.stream()
                .filter(e -> e.getContributor() instanceof HealthIndicator)
                .forEach(e -> bind(e.getName(), (HealthIndicator) e.getContributor(), meterRegistry));
    }

    private void bind(String key, HealthIndicator healthIndicator, MeterRegistry registry) {
        Gauge.builder("health", healthIndicator, statusToDouble()).tag("name", key).register(registry);
    }

    private ToDoubleFunction<HealthIndicator> statusToDouble() {

        return value -> {
            val status = value.health().getStatus().getCode();

            return switch (status) {
                case Health.UP -> 1;
                case Health.PAUSED -> 2;
                case Health.DISABLED -> 3;
                case Health.DOWN, Health.OUT_OF_SERVICE -> 4;
                default -> 5;
            };
        };
    }
}