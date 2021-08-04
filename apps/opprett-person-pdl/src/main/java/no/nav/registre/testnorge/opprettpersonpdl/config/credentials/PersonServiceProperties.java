package no.nav.registre.testnorge.opprettpersonpdl.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.person-api")
public class PersonServiceProperties extends NaisServerProperties {
}
