package no.nav.testnav.apps.personservice.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.pdl-proxy")
public class PdlProxyProperties extends ServerProperties {
}