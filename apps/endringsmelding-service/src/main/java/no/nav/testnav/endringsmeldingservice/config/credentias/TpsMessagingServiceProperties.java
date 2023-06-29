package no.nav.testnav.endringsmeldingservice.config.credentias;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.tps-messaging-service")
public class TpsMessagingServiceProperties extends ServerProperties {
}