package no.nav.registre.testnorge.organisasjon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-organisasjon-api-dev/dev", ignoreSecretNotFound = false)
public class DevVaultConfig {
}