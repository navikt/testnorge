package no.nav.registre.testnorge.personsearchservice.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-pdl-proxy")
public class PdlProxyProperties extends ServerProperties {
}