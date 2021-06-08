package no.nav.dolly.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.dolly.security.oauth2.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-inntektsmelding-service")
public class InntektsmeldingServiceProperties extends NaisServerProperties {
}