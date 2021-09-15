package no.nav.registre.inntekt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.testnav.libs.database.config.FlywayConfiguration;
import no.nav.testnav.libs.database.config.VaultHikariConfiguration;

@Configuration
@Profile("prod")
@Import({
        VaultHikariConfiguration.class,
        FlywayConfiguration.class
})
public class ProdDbConfig {
}
