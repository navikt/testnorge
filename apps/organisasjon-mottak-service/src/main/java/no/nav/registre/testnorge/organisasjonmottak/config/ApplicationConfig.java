package no.nav.registre.testnorge.organisasjonmottak.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Import({
        InsecureJwtServerToServerConfiguration.class,
        ApplicationCoreConfig.class
})
@EnableScheduling
public class ApplicationConfig {
}