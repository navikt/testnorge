package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential;

import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-organisasjon-faste-data-service")
public class OrgFasteDataServiceProperties extends ValidatingServerProperties {
}
