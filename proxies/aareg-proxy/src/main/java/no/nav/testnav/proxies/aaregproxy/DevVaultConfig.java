package no.nav.testnav.proxies.aaregproxy;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Profile("dev")
@Configuration
@VaultPropertySource(value = "kv/preprod/fss/testnav-aareg-proxy/dev", ignoreSecretNotFound = false)
public class DevVaultConfig {
}
