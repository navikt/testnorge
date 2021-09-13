package no.nav.registre.testnorge.originalpopulasjon.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumer.testnav-statistikk-service")
public class StatistikkServiceProperties extends ServerProperties {
}
