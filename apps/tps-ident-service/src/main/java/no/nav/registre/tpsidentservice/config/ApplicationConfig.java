package no.nav.registre.tpsidentservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;

@Configuration
@Import({
        ApplicationCoreConfig.class
})
public class ApplicationConfig {

}
