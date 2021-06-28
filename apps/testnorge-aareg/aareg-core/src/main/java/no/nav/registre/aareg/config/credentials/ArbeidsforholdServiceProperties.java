package no.nav.registre.aareg.config.credentials;


import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "arbeidsforhold-service")
public class ArbeidsforholdServiceProperties extends NaisServerProperties {
}
