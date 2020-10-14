package no.nav.no.registere.testnorge.arbeidsforholdexportapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/arbeidsforhold-export-api/dev", ignoreSecretNotFound = false)
public class DevVaultConfig {
}