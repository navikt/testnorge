package no.nav.testnav.apps.tpsmessagingservice.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.pdl-services")
public class PdlServiceProperties extends ServerProperties {
}