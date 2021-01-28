package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.credentials;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;

@Getter
@Configuration
@ConfigurationProperties(prefix = "consumers.arbeidsforholdapi")
public class ArbeidsforholdApiServerProperties extends NaisServerProperties {
}
