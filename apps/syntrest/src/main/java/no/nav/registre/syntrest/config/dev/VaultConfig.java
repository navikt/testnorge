package no.nav.registre.syntrest.config.dev;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/syntrest/dev", ignoreSecretNotFound = false)
public class VaultConfig {
}
