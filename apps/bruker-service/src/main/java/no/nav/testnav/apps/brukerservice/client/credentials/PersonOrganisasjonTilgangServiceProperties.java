package no.nav.testnav.apps.brukerservice.client.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-person-organisasjon-tilgang-service")
public class PersonOrganisasjonTilgangServiceProperties extends ServerProperties {
}