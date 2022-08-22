package no.nav.testnav.apps.brukerservice.consumer.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-person-organisasjon-tilgang-service")
public class PersonOrganisasjonTilgangServiceProperties extends ServerProperties {
}