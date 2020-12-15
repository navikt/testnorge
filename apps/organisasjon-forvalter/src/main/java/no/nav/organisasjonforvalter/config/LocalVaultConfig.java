package no.nav.organisasjonforvalter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile("dev")
@RequiredArgsConstructor
@VaultPropertySource(value = "serviceuser/dev/srvtestnorge", propertyNamePrefix = "serviceuser.", ignoreSecretNotFound = false)
class LocalVaultConfig {
}