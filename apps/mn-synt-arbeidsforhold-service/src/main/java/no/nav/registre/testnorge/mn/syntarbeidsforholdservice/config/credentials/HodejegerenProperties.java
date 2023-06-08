package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.hodejegeren")
public class HodejegerenProperties extends ValidatedServerProperties {
}
