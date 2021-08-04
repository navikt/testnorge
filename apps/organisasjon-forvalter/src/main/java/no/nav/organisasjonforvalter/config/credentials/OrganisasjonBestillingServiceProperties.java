package no.nav.organisasjonforvalter.config.credentials;


import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.organisasjon-bestilling-service")
public class OrganisasjonBestillingServiceProperties extends NaisServerProperties {
}
