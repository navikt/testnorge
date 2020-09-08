package no.nav.registre.testnorge.hendelse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import no.nav.registere.testnorge.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.database.config.FlywayConfiguration;
import no.nav.registre.testnorge.database.config.VaultHikariConfiguration;


@EnableJpaAuditing
@Configuration
@Import(value = {
        ApplicationCoreConfig.class,
        VaultHikariConfiguration.class,
        FlywayConfiguration.class
})
public class ApplicationConfig {
}