package no.nav.testnav.proxies.dokarkivproxy.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.dokarkiv")
public class DokarkivProperties extends ServerProperties {

    private static DokarkivProperties copyOf(DokarkivProperties original) {
        var copy = new DokarkivProperties();
        copy.setCluster(original.getCluster());
        copy.setName(original.getName());
        copy.setNamespace(original.getNamespace());
        copy.setUrl(original.getUrl());
        return copy;
    }

    public DokarkivProperties forEnvironment(String env) {
        var replacement = "q2".equals(env) ? "" : '-' + env;
        var copy = DokarkivProperties.copyOf(this);
        copy.setUrl(copy.getUrl().replace("-{env}", replacement));
        copy.setName(copy.getName().replace("-{env}", replacement));
        return copy;
    }

}