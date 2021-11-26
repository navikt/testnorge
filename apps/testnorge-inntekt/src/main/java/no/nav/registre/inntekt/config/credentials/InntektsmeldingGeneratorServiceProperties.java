package no.nav.registre.inntekt.config.credentials;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.inntektsmelding-generator-service")
public class InntektsmeldingGeneratorServiceProperties extends ServerProperties {
}
