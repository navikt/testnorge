package no.nav.registre.testnorge.libs.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.config.LoggingWebConfig;
import no.nav.registre.testnorge.libs.core.provider.InternalController;

@Configuration
@Import({
        InternalController.class,
        ApplicationProperties.class,
        LoggingWebConfig.class
})
public class ApplicationCoreConfig {
}