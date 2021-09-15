package no.nav.adresse.service.config.credentials;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.pdl-services")
public class PdlServiceProperties extends ServerProperties {
}