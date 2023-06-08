package no.nav.testnav.proxies.synthdatameldekortproxy.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.synt-meldekort")
public class SyntMeldekortProperties extends ValidatedServerProperties {
}
