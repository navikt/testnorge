package no.nav.registre.testnorge.originalpopulasjon.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumer.synt-person-api")
public class SyntPersonServiceProperties extends NaisServerProperties {
}
