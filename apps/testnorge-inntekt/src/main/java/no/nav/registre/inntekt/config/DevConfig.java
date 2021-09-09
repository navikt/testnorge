package no.nav.registre.inntekt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "azuread/prod/creds/team-dolly-lokal-app", ignoreSecretNotFound = false)
@VaultPropertySource(value = "kv/preprod/fss/testnorge-inntekt/local", ignoreSecretNotFound = false)
public class DevConfig {
}