package no.nav.testnav.libs.servletcore.config;

import no.nav.testnav.libs.servletcore.health.HealthToMeterAutoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ApplicationProperties.class,
        LoggingWebConfig.class,
        HealthToMeterAutoConfig.class
})
public class ApplicationCoreConfig {
}