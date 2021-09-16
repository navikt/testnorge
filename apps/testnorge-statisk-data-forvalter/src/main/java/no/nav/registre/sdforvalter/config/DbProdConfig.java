package no.nav.registre.sdforvalter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.testnav.libs.database.config.FlywayConfiguration;
import no.nav.testnav.libs.database.config.VaultHikariConfiguration;

@Profile("prod")
@Import({
        VaultHikariConfiguration.class,
        FlywayConfiguration.class,
})
@Configuration
public class DbProdConfig {
}
