package no.nav.pdl.forvalter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

import no.nav.registre.testnorge.libs.database.config.FlywayConfiguration;
import no.nav.registre.testnorge.libs.database.config.VaultHikariConfiguration;

@Configuration
@Profile("local")
@Import({
        FlywayConfiguration.class,
        VaultHikariConfiguration.class
})
@VaultPropertySource(value = "azuread/prod/creds/team-dolly-lokal-app", ignoreSecretNotFound = false)
public class LocalConfig {
}