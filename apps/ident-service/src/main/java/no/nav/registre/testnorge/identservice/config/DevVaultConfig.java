package no.nav.registre.testnorge.identservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-ident-service/dev", ignoreSecretNotFound = true)
//TODO tilbake til false over, trenger å opprette source over i vault
public class DevVaultConfig {
}