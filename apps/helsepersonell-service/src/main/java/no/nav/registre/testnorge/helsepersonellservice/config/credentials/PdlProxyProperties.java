package no.nav.registre.testnorge.helsepersonellservice.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-pdl-proxy")
public class PdlProxyProperties extends ServerProperties {
}
