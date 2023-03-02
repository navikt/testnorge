package no.nav.dolly.config.credentials;


import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-dokarkiv-proxy")
public class DokarkivProxyServiceProperties extends ServerProperties {
}
