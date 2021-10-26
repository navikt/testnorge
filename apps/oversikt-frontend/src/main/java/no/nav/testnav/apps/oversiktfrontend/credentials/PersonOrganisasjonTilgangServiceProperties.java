package no.nav.testnav.apps.oversiktfrontend.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-person-organisasjon-tilgang-service")
public class PersonOrganisasjonTilgangServiceProperties extends ServerProperties {
}