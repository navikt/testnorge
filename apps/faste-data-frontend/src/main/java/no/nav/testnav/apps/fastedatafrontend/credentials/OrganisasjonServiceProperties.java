package no.nav.testnav.apps.fastedatafrontend.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-organisasjon-service")
public class OrganisasjonServiceProperties extends NaisServerProperties {
}