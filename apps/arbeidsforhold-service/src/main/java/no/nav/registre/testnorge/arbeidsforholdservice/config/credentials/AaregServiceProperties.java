package no.nav.registre.testnorge.arbeidsforholdservice.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-aareg-proxy")
public class AaregServiceProperties extends NaisServerProperties {
}