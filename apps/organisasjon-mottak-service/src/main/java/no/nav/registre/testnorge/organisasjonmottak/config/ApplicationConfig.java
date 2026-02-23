package no.nav.registre.testnorge.organisasjonmottak.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Import({ApplicationCoreConfig.class})
@EnableScheduling
public class ApplicationConfig {
}