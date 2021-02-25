package no.nav.registre.testnorge.originalpopulasjon.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumer.synt-person-api")
public class SyntPersonServiceProperties extends NaisServerProperties {
}
