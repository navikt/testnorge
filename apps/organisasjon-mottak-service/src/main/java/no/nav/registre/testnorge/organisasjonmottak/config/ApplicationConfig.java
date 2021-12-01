package no.nav.registre.testnorge.organisasjonmottak.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.InsecureJwtServerToServerConfiguration;

@Configuration
@Import({
        InsecureJwtServerToServerConfiguration.class,
        ApplicationCoreConfig.class
})
@EnableScheduling
public class ApplicationConfig {
}