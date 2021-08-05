package no.nav.registre.testnorge.jenkinsbatchstatusservice.config.credentials;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.organisasjon-bestilling-service")
public class OrganisasjonBestillingServiceProperties extends NaisServerProperties {
}
