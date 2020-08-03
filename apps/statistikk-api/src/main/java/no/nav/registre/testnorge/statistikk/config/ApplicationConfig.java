package no.nav.registre.testnorge.statistikk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import no.nav.registere.testnorge.core.ApplicationCoreConfig;

@EnableJpaAuditing
@Configuration
@Import(ApplicationCoreConfig.class)
public class ApplicationConfig {

}
