package no.nav.registre.testnorge.organisasjonservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;

@Configuration
@Import({ApplicationCoreConfig.class})
public class ApplicationConfig {
}
