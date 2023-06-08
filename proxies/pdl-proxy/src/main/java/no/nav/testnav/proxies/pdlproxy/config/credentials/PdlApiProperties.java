package no.nav.testnav.proxies.pdlproxy.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.pdl-api")
public class PdlApiProperties extends ValidatingServerProperties {
}