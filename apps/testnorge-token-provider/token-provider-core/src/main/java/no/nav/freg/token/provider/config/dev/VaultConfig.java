package no.nav.freg.token.provider.config.dev;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-token-provider/local", ignoreSecretNotFound = false)
public class VaultConfig {
}
