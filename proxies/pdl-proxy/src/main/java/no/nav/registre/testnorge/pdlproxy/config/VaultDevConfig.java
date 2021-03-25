package no.nav.registre.testnorge.pdlproxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/testnav-pdl-proxy/dev", ignoreSecretNotFound = false)
public class VaultDevConfig {
}
