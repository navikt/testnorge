package no.nav.tpsidenter.vedlikehold.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "kv/preprod/fss/tps-identer-vedlikehold/local", ignoreSecretNotFound = false)
public class VaultLocalConfig {
}