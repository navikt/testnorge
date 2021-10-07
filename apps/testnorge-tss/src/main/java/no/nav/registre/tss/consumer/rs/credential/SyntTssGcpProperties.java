package no.nav.registre.tss.consumer.rs.credential;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.synt-tss-gcp")
public class SyntTssGcpProperties extends ServerProperties {
}
