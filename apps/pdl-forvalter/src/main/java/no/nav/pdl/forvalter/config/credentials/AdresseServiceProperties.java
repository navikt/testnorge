package no.nav.pdl.forvalter.config.credentials;

import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.adresse-service")
public class AdresseServiceProperties extends ServerProperties {
}