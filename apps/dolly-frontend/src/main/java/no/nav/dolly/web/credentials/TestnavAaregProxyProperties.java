package no.nav.dolly.web.credentials;

import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-aareg-proxy")
public class TestnavAaregProxyProperties extends ValidatingServerProperties {
}