package no.nav.brregstub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import no.nav.registre.testnorge.libs.database.config.VaultHikariConfiguration;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories()
@Import(value = {
        VaultHikariConfiguration.class
})
public class AppConfig {
}
