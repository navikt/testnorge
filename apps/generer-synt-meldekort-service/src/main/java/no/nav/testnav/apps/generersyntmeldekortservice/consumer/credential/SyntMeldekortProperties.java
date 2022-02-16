package no.nav.testnav.apps.generersyntmeldekortservice.consumer.credential;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.synt-meldekort")
public class SyntMeldekortProperties extends ServerProperties {
}
