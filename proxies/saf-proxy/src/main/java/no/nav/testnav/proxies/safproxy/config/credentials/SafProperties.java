package no.nav.testnav.proxies.safproxy.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.saf")
public class SafProperties extends ServerProperties {

    private static SafProperties copyOf(SafProperties original) {
        var copy = new SafProperties();
        copy.setCluster(original.getCluster());
        copy.setName(original.getName());
        copy.setNamespace(original.getNamespace());
        copy.setUrl(original.getUrl());
        return copy;
    }

    public ServerProperties forEnvironment(String env) {
        var replacement = "q2".equals(env) ? "" : '-' + env;
        var copy = SafProperties.copyOf(this);
        copy.setUrl(copy.getUrl().replace("-{env}", replacement));
        copy.setName(copy.getName().replace("-{env}", replacement));
        return copy;
    }

}