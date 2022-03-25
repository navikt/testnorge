package no.nav.testnav.apps.syntvedtakshistorikkservice.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.InsecureJwtServerToServerConfiguration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        ApplicationCoreConfig.class,
        InsecureJwtServerToServerConfiguration.class
})
public class ApplicationConfig {
}
