package no.nav.registre.tp.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class AppConfig {
}
