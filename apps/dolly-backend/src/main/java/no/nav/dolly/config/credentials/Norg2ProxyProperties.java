package no.nav.dolly.config.credentials;

import no.nav.dolly.security.config.NaisServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-norg2-proxy")
public class Norg2ProxyProperties extends NaisServerProperties {
}