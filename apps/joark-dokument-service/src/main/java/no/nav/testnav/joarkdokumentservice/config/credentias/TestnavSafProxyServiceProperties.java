package no.nav.testnav.joarkdokumentservice.config.credentias;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-saf-proxy")
public class TestnavSafProxyServiceProperties extends NaisServerProperties {
}