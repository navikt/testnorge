package no.nav.dolly.bestilling.aareg.credentials;


import no.nav.dolly.security.config.NaisServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-arbeidsforhold-service")
public class ArbeidsforholdServiceProperties extends NaisServerProperties {
}
