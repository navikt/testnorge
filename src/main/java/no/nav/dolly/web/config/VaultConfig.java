package no.nav.dolly.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/dolly/local", ignoreSecretNotFound = false)
public class VaultConfig {
}