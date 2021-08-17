package no.nav.dolly.web.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-testnorge-aareg-proxy")
public class TestnavTestnorgeAaregProxyProperties extends NaisServerProperties {
}