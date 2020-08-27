package no.nav.registre.testnorge.statistikk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import no.nav.registere.testnorge.core.ApplicationCoreConfig;
import no.nav.registre.testnorge.common.config.FlywayConfiguration;
import no.nav.registre.testnorge.common.config.VaultHikariConfiguration;

@EnableJpaAuditing
@Configuration
@Import(value = {
        ApplicationCoreConfig.class,
        VaultHikariConfiguration.class,
        FlywayConfiguration.class
})
public class ApplicationConfig {

}
