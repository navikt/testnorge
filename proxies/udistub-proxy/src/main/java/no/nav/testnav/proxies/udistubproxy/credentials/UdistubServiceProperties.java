package no.nav.testnav.proxies.udistubproxy.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-udistub")
public class UdistubServiceProperties extends ServerProperties {
}