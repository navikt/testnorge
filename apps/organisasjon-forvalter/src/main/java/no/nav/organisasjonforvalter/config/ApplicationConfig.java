package no.nav.organisasjonforvalter.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ApplicationCoreConfig.class})
@Configuration
public class ApplicationConfig {

}
