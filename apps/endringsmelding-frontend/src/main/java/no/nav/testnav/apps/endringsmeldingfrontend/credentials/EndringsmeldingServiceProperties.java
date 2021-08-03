package no.nav.testnav.apps.endringsmeldingfrontend.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.endringsmelding-service")
public class EndringsmeldingServiceProperties extends NaisServerProperties {
}