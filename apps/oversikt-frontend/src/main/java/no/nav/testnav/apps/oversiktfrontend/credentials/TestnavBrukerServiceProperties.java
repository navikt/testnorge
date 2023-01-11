package no.nav.testnav.apps.oversiktfrontend.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-bruker-service")
public class TestnavBrukerServiceProperties extends ServerProperties {
}