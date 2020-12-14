package no.nav.brregstub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.database.config.FlywayConfiguration;
import no.nav.registre.testnorge.libs.database.config.VaultHikariConfiguration;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "no.nav.brregstub.database.repository")
@Import(value = {
        ApplicationCoreConfig.class,
        VaultHikariConfiguration.class,
        FlywayConfiguration.class,
})
public class AppConfig {
}
