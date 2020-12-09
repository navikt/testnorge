package no.nav.registre.testnorge.identservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-ident-check-in-prod-service/local", ignoreSecretNotFound = false)
class VaultConfig {

}