package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.credentials;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Getter
@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-generer-organisasjon-populasjon-service")
public class GenererOrganisasjonPopulasjonServerProperties extends ServerProperties {
}
