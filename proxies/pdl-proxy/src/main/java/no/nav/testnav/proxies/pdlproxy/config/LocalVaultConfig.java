package no.nav.testnav.proxies.pdlproxy.config;

import no.nav.testnav.libs.vault.AbstractLocalVaultConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Profile("dev")
@Configuration
@VaultPropertySource(value = "azuread/prod/creds/team-dolly-lokal-app", ignoreSecretNotFound = false)
@VaultPropertySource(value = "kv/preprod/fss/testnav-pdl-proxy/dev", ignoreSecretNotFound = false)
public class LocalVaultConfig extends AbstractLocalVaultConfiguration {
}