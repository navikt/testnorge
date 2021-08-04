package no.nav.pdl.forvalter.config.credentials;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.generer-navn-service")
public class GenererNavnServiceProperties extends NaisServerProperties {
}