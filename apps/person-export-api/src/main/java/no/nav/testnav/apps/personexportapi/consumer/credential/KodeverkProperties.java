package no.nav.testnav.apps.personexportapi.consumer.credential;

import no.nav.testnav.libs.securitycore.domain.ValidatedServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.kodeverk")
public class KodeverkProperties extends ValidatedServerProperties {
}
