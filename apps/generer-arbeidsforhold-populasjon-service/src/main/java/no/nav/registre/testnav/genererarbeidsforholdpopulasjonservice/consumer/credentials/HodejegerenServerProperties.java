package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.credentials;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-hodejegeren-proxy")
public class HodejegerenServerProperties extends ServerProperties {
}
