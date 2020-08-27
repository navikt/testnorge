package no.nav.registre.testnorge.rapportering.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import no.nav.registere.testnorge.core.ApplicationCoreConfig;
import no.nav.registre.testnorge.common.config.FlywayConfiguration;
import no.nav.registre.testnorge.common.config.VaultHikariConfiguration;

@EnableJpaAuditing
@Configuration
@EnableScheduling
@Import(value = {
        ApplicationCoreConfig.class,
        VaultHikariConfiguration.class,
        FlywayConfiguration.class
})
public class AppConfig {
}
