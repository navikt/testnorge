package no.nav.registre.testnav.inntektsmeldingservice.config.credentials;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-dokarkiv-proxy")
public class DokarkivProxyServiceProperties extends ServerProperties {
}
