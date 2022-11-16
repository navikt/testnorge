package no.nav.registre.sdforvalter.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.tps-forvalteren-proxy")
public class TpsfProxyProperties extends ServerProperties {
}
