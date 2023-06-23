package no.nav.testnav.proxies.arenaforvalterenproxy;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.arena.services")
public class ArenaServerProperties extends ServerProperties {

    private static ArenaServerProperties copyOf(ArenaServerProperties original) {

        var copy = new ArenaServerProperties();
        copy.setCluster(original.getCluster());
        copy.setName(original.getName());
        copy.setNamespace(original.getNamespace());
        copy.setUrl(original.getUrl());
        return copy;
    }

    ArenaServerProperties forEnvironment(String env) {

        var copy = copyOf(this);
        copy.setUrl(copy.getUrl().replace("{env}", env));
        return copy;
    }
}
