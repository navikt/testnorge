package no.nav.pdl.forvalter.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.adresse-service")
public class AdresseServiceProperties extends ValidatedServerProperties {
}