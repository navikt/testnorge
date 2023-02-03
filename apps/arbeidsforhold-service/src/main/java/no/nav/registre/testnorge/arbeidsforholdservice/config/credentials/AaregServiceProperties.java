package no.nav.registre.testnorge.arbeidsforholdservice.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-aareg-proxy")
public class AaregServiceProperties extends ServerProperties {
}