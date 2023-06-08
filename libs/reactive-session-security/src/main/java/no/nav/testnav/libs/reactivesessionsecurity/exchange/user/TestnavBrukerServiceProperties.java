package no.nav.testnav.libs.reactivesessionsecurity.exchange.user;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-bruker-service")
public class TestnavBrukerServiceProperties extends ValidatedServerProperties {
}