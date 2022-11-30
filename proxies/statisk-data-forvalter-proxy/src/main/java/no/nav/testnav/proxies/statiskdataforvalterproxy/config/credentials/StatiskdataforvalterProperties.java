package no.nav.testnav.proxies.statiskdataforvalterproxy.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.statiskdataforvalter")
public class StatiskdataforvalterProperties extends ServerProperties {
}
