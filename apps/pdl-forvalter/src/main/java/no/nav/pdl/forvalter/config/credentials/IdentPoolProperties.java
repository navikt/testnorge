package no.nav.pdl.forvalter.config.credentials;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.ident-pool")
public class IdentPoolProperties extends ServerProperties {
}