package no.nav.dolly.web.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.dolly-backend")
public class DollyBackendProperties extends ValidatingServerProperties {
}