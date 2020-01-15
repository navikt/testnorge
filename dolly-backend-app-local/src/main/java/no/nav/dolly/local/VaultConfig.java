package no.nav.dolly.local;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "/kv/preprod/fss/dolly-backend/t1", ignoreSecretNotFound = false)
@VaultPropertySource(value = "/serviceuser/dev/srvfregdolly", propertyNamePrefix = "jira.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "/serviceuser/test/srvdolly-backend", propertyNamePrefix = "credentials.test.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "/serviceuser/dev/srvdolly-preprod-env", propertyNamePrefix = "credentials.preprod.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "/oracle/data/dev/creds/dolly_t1-user", propertyNamePrefix = "spring.datasource.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "/oracle/dev/config/dolly_t1", propertyNamePrefix = "spring.datasource.", ignoreSecretNotFound = false)
class VaultConfig {

}