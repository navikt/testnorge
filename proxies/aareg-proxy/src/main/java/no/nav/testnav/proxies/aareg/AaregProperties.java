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

        private static AaregServerProperties copyOf(AaregServerProperties original) {
            var copy = new AaregServerProperties();
            copy.setCluster(original.getCluster());
            copy.setName(original.getName());
            copy.setNamespace(original.getNamespace());
            copy.setUrl(original.getUrl());
            return copy;
        }

        AaregServerProperties forEnvironment(String env) {
            var replacement = "q2".equals(env) ? "" : '-' + env;
            var copy = copyOf(this);
            copy.setUrl(copy.getUrl().replace("-{env}", replacement));
            copy.setName(copy.getName().replace("-{env}", replacement));
            return copy;
        }

    }
}
