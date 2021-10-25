package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.syntrest-proxy")
public class SyntrestServiceProperties extends ServerProperties {
}
