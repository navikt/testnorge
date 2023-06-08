package no.nav.dolly.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-kodeverk-proxy")
public class KodeverkProxyProperties extends ValidatingServerProperties {
}