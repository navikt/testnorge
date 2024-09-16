package no.nav.registre.sdforvalter.config;

import no.nav.testnav.libs.vault.AbstractLocalVaultConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-statisk-data-forvalter/local", ignoreSecretNotFound = false)
public class LocalVaultConfig extends AbstractLocalVaultConfiguration {
}