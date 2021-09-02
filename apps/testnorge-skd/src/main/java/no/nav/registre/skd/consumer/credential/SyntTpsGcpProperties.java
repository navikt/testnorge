package no.nav.registre.skd.consumer.credential;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.synt-tps-gcp")
public class SyntTpsGcpProperties extends NaisServerProperties {
}
