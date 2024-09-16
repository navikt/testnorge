package no.nav.testnav.apps.tpsmessagingservice.config;

import no.nav.testnav.libs.vault.AbstractLocalVaultConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "serviceuser/dev/srvtestnav-tps-messa", propertyNamePrefix = "credentials.mq.", ignoreSecretNotFound = false)
public class LocalVaultConfig extends AbstractLocalVaultConfiguration {
}