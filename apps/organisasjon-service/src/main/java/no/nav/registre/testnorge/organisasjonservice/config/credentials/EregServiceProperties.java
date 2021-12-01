package no.nav.registre.testnorge.organisasjonservice.config.credentials;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-ereg-proxy")
public class EregServiceProperties extends ServerProperties {
}
