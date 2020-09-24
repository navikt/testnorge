package no.nav.registre.testnorge.opprettpersonpdl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-opprett-person-pdl/dev", ignoreSecretNotFound = false)
public class DevVaultConfig {
}