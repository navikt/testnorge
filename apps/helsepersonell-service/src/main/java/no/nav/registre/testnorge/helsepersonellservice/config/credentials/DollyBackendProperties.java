package no.nav.registre.testnorge.helsepersonellservice.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.dolly-backend")
public class DollyBackendProperties extends ServerProperties {
}
