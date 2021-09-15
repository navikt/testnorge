package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.credentials;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.generer-navn-service")
public class GenererNavnServiceProperties extends ServerProperties {
}
