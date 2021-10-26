package no.nav.registre.varslingerservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.database.config.FlywayConfiguration;
import no.nav.testnav.libs.database.config.VaultHikariConfiguration;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;

@EnableJpaAuditing
@Configuration
@Import({
        ApplicationCoreConfig.class,
        FlywayConfiguration.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class ApplicationConfig {
}
