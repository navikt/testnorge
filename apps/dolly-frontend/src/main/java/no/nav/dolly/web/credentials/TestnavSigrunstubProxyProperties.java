package no.nav.dolly.web.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.NaisServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-sigrunstub-proxy")
public class TestnavSigrunstubProxyProperties extends NaisServerProperties {
}