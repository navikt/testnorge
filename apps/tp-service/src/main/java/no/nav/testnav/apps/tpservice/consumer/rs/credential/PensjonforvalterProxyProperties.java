package no.nav.testnav.apps.tpservice.consumer.rs.credential;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-pensjon-testdata-facade-proxy")
public class PensjonforvalterProxyProperties extends ServerProperties {
}