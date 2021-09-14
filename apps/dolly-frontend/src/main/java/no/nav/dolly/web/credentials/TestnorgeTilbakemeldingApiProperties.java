package no.nav.dolly.web.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesessionsecurity.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnorge-tilbakemelding-api")
public class TestnorgeTilbakemeldingApiProperties extends ServerProperties {
}