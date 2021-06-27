package no.nav.pdl.forvalter.config;

import no.nav.registre.testnorge.libs.database.config.FlywayConfiguration;
import no.nav.registre.testnorge.libs.database.config.VaultHikariConfiguration;
import no.nav.registre.testnorge.libs.localdevelopment.LocalDevelopmentConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
@Import({LocalDevelopmentConfig.class,
        FlywayConfiguration.class,
        VaultHikariConfiguration.class})
public class LocalConfig {
}