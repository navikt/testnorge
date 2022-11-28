package no.nav.registre.sdforvalter.consumer.rs.credential;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnorge-tp")
public class TestnorgeTpProperties extends ServerProperties {
}
