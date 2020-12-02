package no.nav.registre.testnorge.helsepersonell.config.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.core.VaultTemplate;

import javax.annotation.PostConstruct;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@VaultPropertySource(value = "kv/preprod/fss/testnorge-helsepersonell-api/local", ignoreSecretNotFound = false)
public class VaultConfig {
}