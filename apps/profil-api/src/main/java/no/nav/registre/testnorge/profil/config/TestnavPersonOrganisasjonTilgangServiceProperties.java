package no.nav.registre.testnorge.profil.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-person-organisasjon-tilgang-service")
public class TestnavPersonOrganisasjonTilgangServiceProperties extends ServerProperties {
}