package no.nav.registre.orgnrservice.config.credentials;


import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-miljoer-service")
public class MiljoerServiceProperties extends NaisServerProperties {
}
