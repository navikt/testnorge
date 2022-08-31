package no.nav.testnav.proxies.dokarkivproxy.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.dokarkivq1")
public class DokarkivQ1Properties extends ServerProperties {
}