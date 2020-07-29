package no.nav.registre.sdforvalter.config.dev;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-statisk-data-forvalter/local", ignoreSecretNotFound = false)
public class VaultConfig {
}