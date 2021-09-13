package no.nav.pdl.forvalter.config.credentials;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.generer-navn-service")
public class GenererNavnServiceProperties extends ServerProperties {
}