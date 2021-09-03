package no.nav.registre.aareg.config.credentials;


import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "miljoer-service")
public class MiljoeServiceProperties extends ServerProperties {
}
