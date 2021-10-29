package no.nav.dolly.config.credentials;


import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-dokarkiv-proxy")
public class DokarkivProxyServiceProperties extends NaisServerProperties {
}
