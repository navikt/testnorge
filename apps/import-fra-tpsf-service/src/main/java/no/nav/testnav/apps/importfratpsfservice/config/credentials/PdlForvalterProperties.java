package no.nav.testnav.apps.importfratpsfservice.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.pdl-forvalter")
public class PdlForvalterProperties extends ServerProperties {
}