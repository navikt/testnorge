package no.nav.dolly.web.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-organisasjon-service")
public class TestnavOrganisasjonServiceProperties extends ServerProperties {
}