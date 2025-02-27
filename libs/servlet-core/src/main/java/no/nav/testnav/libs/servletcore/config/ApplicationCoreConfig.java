package no.nav.testnav.libs.servletcore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ApplicationProperties.class,
        LoggingWebConfig.class
})
public class ApplicationCoreConfig {
}