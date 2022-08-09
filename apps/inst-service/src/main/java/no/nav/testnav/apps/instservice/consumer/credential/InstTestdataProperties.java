package no.nav.testnav.apps.instservice.consumer.credential;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.inst-testdata")
public class InstTestdataProperties extends ServerProperties {
}
