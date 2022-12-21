package no.nav.testnav.proxies.popptestdata.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.popp-testdata")
public class PoppTestdataProperties extends ServerProperties {
}
