package no.nav.testnav.proxies.kontoregisterperson.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.kontoregister")
public class KontoregisterProperties extends ServerProperties {
    @Override
    public String toAzureAdScope() {
        return "api://d750bf57-b75f-425b-b8df-24a0c3673768/.default";
    }
}
