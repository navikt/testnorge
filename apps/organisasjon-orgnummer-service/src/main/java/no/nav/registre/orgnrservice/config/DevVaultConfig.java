package no.nav.registre.orgnrservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
//import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
//@VaultPropertySource(value = "kv/preprod/fss/testnorge-tilbakemelding-api/dev", ignoreSecretNotFound = false)
public class DevVaultConfig {
}