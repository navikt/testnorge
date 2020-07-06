package no.nav.registre.skd.config.dev;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-skd/local", ignoreSecretNotFound = false)
public class VaultConfig {
}