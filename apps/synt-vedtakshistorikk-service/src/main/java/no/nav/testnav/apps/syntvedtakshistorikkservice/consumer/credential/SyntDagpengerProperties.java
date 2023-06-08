package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.synt-dagpenger")
public class SyntDagpengerProperties extends ValidatedServerProperties {
}
