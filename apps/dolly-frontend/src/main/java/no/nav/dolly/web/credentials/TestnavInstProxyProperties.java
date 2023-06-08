package no.nav.dolly.web.credentials;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-inst-proxy")
public class TestnavInstProxyProperties extends ValidatedServerProperties {
}