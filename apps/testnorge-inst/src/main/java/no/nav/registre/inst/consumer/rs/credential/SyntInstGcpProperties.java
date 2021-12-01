package no.nav.registre.inst.consumer.rs.credential;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.synt-inst-gcp")
public class SyntInstGcpProperties extends ServerProperties {
}
