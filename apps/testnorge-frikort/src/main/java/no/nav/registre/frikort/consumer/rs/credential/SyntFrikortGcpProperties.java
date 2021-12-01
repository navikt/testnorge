package no.nav.registre.frikort.consumer.rs.credential;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.synt-frikort-gcp")
public class SyntFrikortGcpProperties extends ServerProperties {
}
