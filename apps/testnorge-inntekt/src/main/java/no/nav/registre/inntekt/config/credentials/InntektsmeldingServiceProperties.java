package no.nav.registre.inntekt.config.credentials;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-inntektsmelding-service")
public class InntektsmeldingServiceProperties extends ServerProperties {
}
