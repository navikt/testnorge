package no.nav.pdl.forvalter.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.geografiske-kodeverk-service")
public class GeografiskeKodeverkServiceProperties extends ValidatingServerProperties {
}
