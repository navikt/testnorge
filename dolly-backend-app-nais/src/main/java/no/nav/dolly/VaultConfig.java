package no.nav.dolly;

import org.springframework.context.annotation.Configuration;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@VaultPropertySource(value = "kv/preprod/fss/dolly-backend/local", ignoreSecretNotFound = false)
@VaultPropertySource(value = "serviceuser/test/srvdolly-backend", propertyNamePrefix = "credentials.test.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "serviceuser/dev/srvdolly-preprod-env", propertyNamePrefix = "credentials.preprod.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "serviceuser/dev/srvfregdolly", propertyNamePrefix = "jira.", ignoreSecretNotFound = false)
class VaultConfig {

}