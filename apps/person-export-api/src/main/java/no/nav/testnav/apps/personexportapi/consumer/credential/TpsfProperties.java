package no.nav.testnav.apps.personexportapi.consumer.credential;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.tps-forvalter")
public class TpsfProperties extends ServerProperties {
}
