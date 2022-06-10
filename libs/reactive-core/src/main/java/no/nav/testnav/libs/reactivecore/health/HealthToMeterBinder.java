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
            if (no.nav.testnav.libs.reactivecore.health.Health.UP.equals(status)) {
                return 1;
            } else if (no.nav.testnav.libs.reactivecore.health.Health.PAUSED.equals(status)) {
                return 2;
            } else if (no.nav.testnav.libs.reactivecore.health.Health.DISABLED.equals(status)) {
                return 3;
            } else if (no.nav.testnav.libs.reactivecore.health.Health.DOWN.equals(status)) {
                return 4;
            } else if (no.nav.testnav.libs.reactivecore.health.Health.OUT_OF_SERVICE.equals(status)) {
                return 4;
            }
            return 5;
        };
    }
}