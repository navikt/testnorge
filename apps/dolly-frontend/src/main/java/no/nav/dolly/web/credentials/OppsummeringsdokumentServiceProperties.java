package no.nav.dolly.web.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.oppsummeringsdokument-service")
public class OppsummeringsdokumentServiceProperties extends ServerProperties {
}