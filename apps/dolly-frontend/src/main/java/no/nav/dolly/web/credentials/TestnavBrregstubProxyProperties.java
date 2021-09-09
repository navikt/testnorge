package no.nav.dolly.web.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-brregstub-proxy")
public class TestnavBrregstubProxyProperties extends ServerProperties {
}