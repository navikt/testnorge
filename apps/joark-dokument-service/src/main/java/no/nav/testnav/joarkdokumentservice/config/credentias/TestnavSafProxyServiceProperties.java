package no.nav.testnav.joarkdokumentservice.config.credentias;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-saf-proxy")
public class TestnavSafProxyServiceProperties extends ServerProperties {
}