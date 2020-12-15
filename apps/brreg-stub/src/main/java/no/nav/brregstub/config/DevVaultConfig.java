package no.nav.brregstub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "kv/preprod/fss/brreg-stub/local", ignoreSecretNotFound = false)
public class DevVaultConfig {
}