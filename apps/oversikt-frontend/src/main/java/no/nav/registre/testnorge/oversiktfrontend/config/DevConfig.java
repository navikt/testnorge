package no.nav.registre.testnorge.oversiktfrontend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

import no.nav.registre.testnorge.libs.localdevelopment.LocalDevelopmentConfig;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "azuread/prod/creds/team-dolly-lokal-app", ignoreSecretNotFound = false)
public class DevConfig {
}
