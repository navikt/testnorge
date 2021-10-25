package no.nav.dolly.web.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-krrstub-proxy")
public class TestnavKrrstubProxyProperties extends ServerProperties {
}