package no.nav.testnav.proxies.samhandlerregisteretproxy.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.samhandlerregisteret")
public class SamhandlerregisteretProperties extends ServerProperties {
}
