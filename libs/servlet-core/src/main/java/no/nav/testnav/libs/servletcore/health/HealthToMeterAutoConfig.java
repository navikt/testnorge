package no.nav.testnav.libs.servletcore.health;

import org.springframework.boot.actuate.autoconfigure.health.HealthContributorAutoConfiguration;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.SimpleStatusAggregator;
import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(HealthContributorAutoConfiguration.class)
public class HealthToMeterAutoConfig {

    @Bean
    static HealthToMeterBinder healthToMeterBinder(HealthContributorRegistry registry) {
        return new HealthToMeterBinder(registry);
    }

    @Bean
    static StatusAggregator statusAggregator() {
        return new SimpleStatusAggregator(List.of(
                Health.DOWN,
                Health.OUT_OF_SERVICE,
                Health.DISABLED,
                Health.PAUSED,
                Health.UP
        ));
    }
}
