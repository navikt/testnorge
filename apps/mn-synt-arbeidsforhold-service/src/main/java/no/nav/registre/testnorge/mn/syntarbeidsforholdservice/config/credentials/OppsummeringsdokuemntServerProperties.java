package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.credentials;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;

@Getter
@Configuration
@ConfigurationProperties(prefix = "consumers.oppsummeringsdokument-service")
public class OppsummeringsdokuemntServerProperties extends NaisServerProperties {
}
