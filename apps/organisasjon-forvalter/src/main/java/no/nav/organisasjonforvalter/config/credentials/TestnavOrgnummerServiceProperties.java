package no.nav.organisasjonforvalter.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-orgnummer-service")
public class TestnavOrgnummerServiceProperties extends ServerProperties {
}
