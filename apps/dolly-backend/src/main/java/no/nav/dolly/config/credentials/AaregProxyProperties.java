package no.nav.dolly.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-aareg-proxy")
public class AaregProxyProperties extends ValidatedServerProperties {
}