package no.nav.organisasjonforvalter.config.credentials;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.organisasjon-bestilling-service")
public class OrganisasjonBestillingServiceProperties extends ValidatingServerProperties {
}
