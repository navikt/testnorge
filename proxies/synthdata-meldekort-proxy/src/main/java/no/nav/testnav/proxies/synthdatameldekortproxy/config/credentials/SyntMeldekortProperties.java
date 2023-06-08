package no.nav.testnav.proxies.synthdatameldekortproxy.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.synt-meldekort")
public class SyntMeldekortProperties extends ValidatingServerProperties {
}
