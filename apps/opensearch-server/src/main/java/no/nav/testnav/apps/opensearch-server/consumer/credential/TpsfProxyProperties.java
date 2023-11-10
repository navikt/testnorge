package no.nav.testnav.apps.hodejegeren.consumer.credential;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.tpsf-proxy")
public class TpsfProxyProperties extends ServerProperties {
}