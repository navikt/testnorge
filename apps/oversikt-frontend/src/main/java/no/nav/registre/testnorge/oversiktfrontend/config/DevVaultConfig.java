package no.nav.registre.testnorge.oversiktfrontend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-oversikt-frontend/dev", ignoreSecretNotFound = false)
public class DevVaultConfig {
}