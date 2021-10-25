package no.nav.testnav.proxies.udistubproxy.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-udistub-dev")
public class UdistubDevServiceProperties extends ServerProperties {
}