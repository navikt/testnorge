package no.nav.testnav.libs.servletcore.config;

import no.nav.testnav.libs.servletcore.health.HealthToMeterAutoConfig;
import no.nav.testnav.libs.servletcore.provider.InternalController;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        InternalController.class,
        ApplicationProperties.class,
        LoggingWebConfig.class,
        HealthToMeterAutoConfig.class
})
public class ApplicationCoreConfig {
}