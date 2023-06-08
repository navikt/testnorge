package no.nav.registre.bisys.consumer.credential;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.synt-bisys")
@Getter
@Setter
public class SyntBisysProperties implements ServerProperties {
    private String cluster;
    private String namespace;
    private String name;
    private String url;


}
