package no.nav.organisasjonforvalter.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class})
@Configuration
public class ApplicationConfig {

}
