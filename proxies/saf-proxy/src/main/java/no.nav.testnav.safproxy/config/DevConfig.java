package no.nav.testnav.safproxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "azuread/prod/creds/team-dolly-lokal-app", ignoreSecretNotFound = false)
@VaultPropertySource(value = "kv/preprod/fss/testnav-saf-proxy/dev", ignoreSecretNotFound = false)
public class DevConfig {
}
