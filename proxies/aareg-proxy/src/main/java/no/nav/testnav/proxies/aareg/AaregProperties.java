package no.nav.testnav.proxies.aareg;

import lombok.Setter;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.aareg")
@Setter
public class AaregProperties {
    AaregServerProperties services;
    AaregServerProperties vedlikehold;

    static class AaregServerProperties extends ServerProperties {
        ServerProperties forEnvironment(String env) {

            var replacement = "q2" .equals(env) ? "" : '-' + env;
            return new ServerProperties(
                    getUrl().replace("-{env}", replacement),
                    getCluster(),
                    getName().replace("-{env}", replacement),
                    getNamespace());
        }
    }
}
