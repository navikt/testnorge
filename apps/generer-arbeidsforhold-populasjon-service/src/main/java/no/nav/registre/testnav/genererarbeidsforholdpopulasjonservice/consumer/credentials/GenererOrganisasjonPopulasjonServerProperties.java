package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.credentials;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;

@Getter
@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-generer-organisasjon-populasjon-service")
public class GenererOrganisasjonPopulasjonServerProperties extends NaisServerProperties {
}
