package no.nav.registre.bisys.consumer.credential;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.person-search-service")
public class PersonSearchProperties extends ServerProperties {
}
