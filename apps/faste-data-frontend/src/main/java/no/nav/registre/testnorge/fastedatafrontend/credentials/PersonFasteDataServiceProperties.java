package no.nav.registre.testnorge.fastedatafrontend.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.security.domain.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-person-faste-data-service")
public class PersonFasteDataServiceProperties extends NaisServerProperties {
}