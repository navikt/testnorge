package no.nav.testnav.apps.syntsykemeldingapi.config.credentials;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-organisasjon-service")
public class OrganisasjonServiceProperties extends ServerProperties {

}
