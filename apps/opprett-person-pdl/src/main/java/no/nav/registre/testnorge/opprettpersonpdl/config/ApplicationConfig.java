package no.nav.registre.testnorge.opprettpersonpdl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.InsecureOAuth2ServerToServerConfiguration;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        InsecureOAuth2ServerToServerConfiguration.class
})
public class ApplicationConfig {
}
