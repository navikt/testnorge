package no.nav.registre.aareg;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-aareg/local", ignoreSecretNotFound = false)
public class LocalVaultConfig {
}