package no.nav.registre.tpsidentservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;

@Configuration
@Import({
        ApplicationCoreConfig.class
})
public class ApplicationConfig {

}
