package no.nav.registre.testnorge.generersyntameldingservice.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.syntrest-proxy")
public class SyntrestProperties extends NaisServerProperties {
}
