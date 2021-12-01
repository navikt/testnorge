package no.nav.registre.medl.consumer.rs.credential;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.synt-medl-gcp")
public class SyntMedlGcpProperties extends ServerProperties {
}
