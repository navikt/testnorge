package no.nav.testnav.apps.personservice.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.security.domain.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.pdl-service")
public class PdlServiceProperties extends NaisServerProperties {
}