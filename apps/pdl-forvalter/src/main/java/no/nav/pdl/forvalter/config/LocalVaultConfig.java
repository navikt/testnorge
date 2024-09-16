package no.nav.pdl.forvalter.config;

import no.nav.testnav.libs.database.config.FlywayConfiguration;
import no.nav.testnav.libs.database.config.VaultHikariConfiguration;
import no.nav.testnav.libs.vault.AbstractLocalVaultConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
@Import({
        FlywayConfiguration.class,
        VaultHikariConfiguration.class
})
public class LocalVaultConfig extends AbstractLocalVaultConfiguration {
}