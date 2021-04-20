package no.nav.pdl.forvalter.config;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.database.config.FlywayConfiguration;
import no.nav.registre.testnorge.libs.database.config.VaultHikariConfiguration;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ApplicationCoreConfig.class,
        FlywayConfiguration.class,
        VaultHikariConfiguration.class,
        SecureOAuth2ServerToServerConfiguration.class})
public class ApplicationConfig {
}