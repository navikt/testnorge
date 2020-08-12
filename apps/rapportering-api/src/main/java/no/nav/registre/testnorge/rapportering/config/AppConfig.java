package no.nav.registre.testnorge.rapportering.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import no.nav.registere.testnorge.core.ApplicationCoreConfig;

@EnableJpaAuditing
@Configuration
@EnableScheduling
@Import(ApplicationCoreConfig.class)
public class AppConfig {
}
