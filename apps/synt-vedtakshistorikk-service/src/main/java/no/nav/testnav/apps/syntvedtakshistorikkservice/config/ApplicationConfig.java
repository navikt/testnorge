package no.nav.testnav.apps.syntvedtakshistorikkservice.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;

@Configuration
@Import(value = {
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class ApplicationConfig {
}
