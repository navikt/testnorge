package no.nav.registre.testnorge.arena.config.dev;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-arena/local", ignoreSecretNotFound = false)
public class VaultConfig {
}
