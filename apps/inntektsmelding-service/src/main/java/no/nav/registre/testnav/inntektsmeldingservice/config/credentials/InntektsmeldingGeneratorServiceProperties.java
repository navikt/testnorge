package no.nav.registre.testnav.inntektsmeldingservice.config.credentials;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.inntektsmelding-generator-service")
public class InntektsmeldingGeneratorServiceProperties extends ServerProperties {
}
