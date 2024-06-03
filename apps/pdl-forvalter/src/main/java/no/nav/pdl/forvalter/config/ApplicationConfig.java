package no.nav.pdl.forvalter.config;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Clock;

@Configuration
@Import({ApplicationCoreConfig.class,
        CoreConfig.class,
        InsecureJwtServerToServerConfiguration.class})
public class ApplicationConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}