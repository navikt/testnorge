package no.nav.dolly;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "kv/preprod/fss/dolly-backend/local", ignoreSecretNotFound = false)
class VaultConfig {

}