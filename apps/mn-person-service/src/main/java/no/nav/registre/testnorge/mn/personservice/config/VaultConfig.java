package no.nav.registre.testnorge.mn.personservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/mn-person-service/dev", ignoreSecretNotFound = false)
public class VaultConfig {
}
