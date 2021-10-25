package no.nav.registre.testnorge.opprettpersonpdl.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.person-service")
public class PersonServiceProperties extends ServerProperties {
}
