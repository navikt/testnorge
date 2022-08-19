package no.nav.testnav.apps.tpservice.consumer.credential;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.synt-tp-gcp")
public class SyntTpProperties extends ServerProperties {
}
