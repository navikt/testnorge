package no.nav.testnav.apps.bankkontoservice.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testmiljoer.service")
public class TestmiljoerServiceProperties extends ServerProperties {
}
