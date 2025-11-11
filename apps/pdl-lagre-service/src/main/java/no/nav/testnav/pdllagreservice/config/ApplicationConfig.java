package no.nav.testnav.pdllagreservice.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@Configuration
@Import(ApplicationCoreConfig.class)
public class ApplicationConfig {

}
