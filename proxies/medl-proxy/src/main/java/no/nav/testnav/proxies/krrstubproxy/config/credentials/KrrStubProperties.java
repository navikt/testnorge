package no.nav.testnav.proxies.krrstubproxy.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.krrstub")
public class KrrStubProperties extends ValidatedServerProperties {
}