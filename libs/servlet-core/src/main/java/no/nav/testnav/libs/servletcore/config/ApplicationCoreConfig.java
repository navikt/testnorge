package no.nav.testnav.libs.servletcore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletcore.provider.InternalController;

@Configuration
@Import({
        InternalController.class,
        ApplicationProperties.class,
        LoggingWebConfig.class
})
public class ApplicationCoreConfig {
}