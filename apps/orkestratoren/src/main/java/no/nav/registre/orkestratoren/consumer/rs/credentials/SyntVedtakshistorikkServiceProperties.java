package no.nav.registre.orkestratoren.consumer.rs.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.synt-vedtakshistorikk-service")
public class SyntVedtakshistorikkServiceProperties extends ServerProperties {
}
