package no.nav.registre.sdforvalter.config.credentials;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.person")
public class PersonServiceProperties extends ServerProperties {
}
