package no.nav.dolly.web.credentials;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-dokarkiv-proxy")
public class TestnavDokarkivProxyProperties extends ValidatedServerProperties {
}
