package no.nav.dolly.web.credentials;

import no.nav.testnav.libs.reactivesessionsecurity.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-pdl-forvalter")
public class TestnavPdlForvalterProperties extends ServerProperties {
}
