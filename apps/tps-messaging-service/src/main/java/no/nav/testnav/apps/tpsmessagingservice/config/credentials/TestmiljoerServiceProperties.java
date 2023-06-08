package no.nav.testnav.apps.tpsmessagingservice.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testmiljoer.service")
public class TestmiljoerServiceProperties extends ValidatedServerProperties {
}