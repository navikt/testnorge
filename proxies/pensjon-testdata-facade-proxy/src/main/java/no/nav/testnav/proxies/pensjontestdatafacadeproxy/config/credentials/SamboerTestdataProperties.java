package no.nav.testnav.proxies.pensjontestdatafacadeproxy.config.credentials;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.samboer-testdata")
public class SamboerTestdataProperties extends ServerProperties {

    private static SamboerTestdataProperties copyOf(SamboerTestdataProperties original) {
        var copy = new SamboerTestdataProperties();
        copy.setCluster(original.getCluster());
        copy.setName(original.getName());
        copy.setNamespace(original.getNamespace());
        copy.setUrl(original.getUrl());
        return copy;
    }

    public SamboerTestdataProperties forEnvironment(String env) {

        var copy = copyOf(this);
        copy.setUrl(copy.getUrl().replace("{env}", env + ("q1".equals(env) ? ".intern" : "")));
        copy.setName(copy.getName().replace("{env}", env));
        copy.setCluster(("q1".equals(env) ? "dev-fss" : copy.getCluster()));
        return copy;
    }
}
