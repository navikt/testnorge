package no.nav.registre.testnorge.batchbestillingservice.credentials;


import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.dolly-backend-dev")
public class DollyBackendDevServiceProperties extends ServerProperties {
}
