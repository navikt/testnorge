package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-pensjon-testdata-facade-proxy")
public class PensjonTestdataFacadeProxyProperties extends ServerProperties {
}
