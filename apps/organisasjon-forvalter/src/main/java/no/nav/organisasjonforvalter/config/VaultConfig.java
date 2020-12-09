package no.nav.organisasjonforvalter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@VaultPropertySource(value = "kv/preprod/fss/organisasjon-forvalter/dolly", ignoreSecretNotFound = false)
public class VaultConfig {
}
