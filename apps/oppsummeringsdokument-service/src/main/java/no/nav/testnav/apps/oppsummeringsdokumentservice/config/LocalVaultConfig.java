package no.nav.testnav.apps.oppsummeringsdokumentservice.config;

import no.nav.testnav.libs.vault.AbstractLocalVaultConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/oppsummeringsdokument-service/dev", ignoreSecretNotFound = false)
public class LocalVaultConfig extends AbstractLocalVaultConfiguration {
}