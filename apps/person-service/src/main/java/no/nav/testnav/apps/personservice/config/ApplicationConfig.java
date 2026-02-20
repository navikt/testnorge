package no.nav.testnav.apps.personservice.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@Import({ApplicationCoreConfig.class})
@EnableAsync
public class ApplicationConfig {

}
