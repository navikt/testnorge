package no.nav.registre.inntekt.config.local;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-inntekt/local", ignoreSecretNotFound = false)
public class VaultConfig {
}