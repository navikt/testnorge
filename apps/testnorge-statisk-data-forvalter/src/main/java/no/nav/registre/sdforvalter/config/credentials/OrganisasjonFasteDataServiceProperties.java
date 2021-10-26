package no.nav.registre.sdforvalter.config.credentials;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-organisasjon-faste-data-service")
public class OrganisasjonFasteDataServiceProperties extends ServerProperties {
}
