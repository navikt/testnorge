package no.nav.registre.testnav.ameldingservice.credentials;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.NaisServerProperties;


@Getter
@Configuration
@ConfigurationProperties(prefix = "consumers.oppsummeringsdokument-service")
public class OppsummeringsdokumentServerProperties extends NaisServerProperties {
}
