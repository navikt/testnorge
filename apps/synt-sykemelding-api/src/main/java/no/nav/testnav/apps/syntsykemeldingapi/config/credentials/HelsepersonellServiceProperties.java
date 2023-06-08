package no.nav.testnav.apps.syntsykemeldingapi.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-helsepersonell-service")
public class HelsepersonellServiceProperties extends ValidatedServerProperties {
}