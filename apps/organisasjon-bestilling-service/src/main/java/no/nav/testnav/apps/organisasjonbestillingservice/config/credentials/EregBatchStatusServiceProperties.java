package no.nav.testnav.apps.organisasjonbestillingservice.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.ereg-batch-status-service")
public class EregBatchStatusServiceProperties extends ValidatedServerProperties {
}