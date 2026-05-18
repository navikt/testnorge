package no.nav.testnav.libs.servletcore.health;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.health.actuate.endpoint.SimpleStatusAggregator;
import org.springframework.boot.health.actuate.endpoint.StatusAggregator;
import org.springframework.boot.health.registry.HealthContributorRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

@AutoConfiguration
@ConditionalOnClass(HealthContributorRegistry.class)
@Profile("!test")
public class HealthAutoConfiguration {

    @Bean
    HealthToMeterBinder healthToMeterBinder(HealthContributorRegistry registry) {
        return new HealthToMeterBinder(registry);
    }

    @Bean
    StatusAggregator statusAggregator() {
        return new SimpleStatusAggregator(List.of(
                Health.DOWN,
                Health.OUT_OF_SERVICE,
                Health.DISABLED,
                Health.PAUSED,
                Health.UP
        ));
    }

    @Bean
    FilterRegistrationBean<LegacyHealthEndpointsForwardingFilter> redirectFilterRegistration() {
        var registrationBean = new FilterRegistrationBean<LegacyHealthEndpointsForwardingFilter>();
        registrationBean.setFilter(new LegacyHealthEndpointsForwardingFilter());
        registrationBean.addUrlPatterns("/internal/isAlive", "/internal/isReady");
        registrationBean.setOrder(1);
        return registrationBean;
    }

}
