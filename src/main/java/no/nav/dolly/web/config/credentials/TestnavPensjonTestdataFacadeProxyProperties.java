package no.nav.dolly.web.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-pensjon-testdata-facade-proxy")
public class TestnavPensjonTestdataFacadeProxyProperties extends NaisServerProperties {
}