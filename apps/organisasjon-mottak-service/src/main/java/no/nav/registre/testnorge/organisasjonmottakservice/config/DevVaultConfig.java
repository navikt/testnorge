package no.nav.registre.testnorge.organisasjonmottakservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/organisasjon-mottak-service/dev", ignoreSecretNotFound = false)
public class DevVaultConfig {
}