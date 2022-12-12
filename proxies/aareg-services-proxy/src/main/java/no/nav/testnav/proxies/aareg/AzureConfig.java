package no.nav.testnav.proxies.aareg;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.azure")
public class AzureConfig extends ServerProperties {

    ServerProperties forEnvironment(String env) {
        return new ServerProperties(
                getUrl().replace("{env}", env),
                getCluster(),
                getName().replace("{env}", env),
                getNamespace());
    }

}
