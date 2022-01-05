package no.nav.registre.syntrest.consumer.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.synt-meldekort")
public class SyntMeldekortProperties extends ServerProperties {
}
