package no.nav.identpool;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "oracle/dev/creds/identpool_test-user", propertyNamePrefix = "oracle.datasource.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "oracle/dev/config/identpool_test", propertyNamePrefix = "identpool.datasource.", ignoreSecretNotFound = false)
class VaultConfig {

}