package no.nav.testnav.apps.organisasjontilgangfrontend.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-organisasjon-tilgang-service")
public class TestnavOrganisasjonTilgangServiceProperties extends ServerProperties {
}