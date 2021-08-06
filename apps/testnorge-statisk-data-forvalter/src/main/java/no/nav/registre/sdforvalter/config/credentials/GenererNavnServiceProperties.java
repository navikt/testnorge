package no.nav.registre.sdforvalter.config.credentials;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.generer-navn-service")
public class GenererNavnServiceProperties extends NaisServerProperties {
}
