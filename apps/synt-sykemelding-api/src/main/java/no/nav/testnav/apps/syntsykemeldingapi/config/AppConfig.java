package no.nav.testnav.apps.syntsykemeldingapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.InsecureJwtServerToServerConfiguration;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        InsecureJwtServerToServerConfiguration.class
})
public class AppConfig {

}
