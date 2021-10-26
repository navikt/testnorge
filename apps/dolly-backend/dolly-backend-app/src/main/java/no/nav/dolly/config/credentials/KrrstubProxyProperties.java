package no.nav.dolly.config.credentials;

import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-krrstub-proxy")
public class KrrstubProxyProperties extends NaisServerProperties {
}