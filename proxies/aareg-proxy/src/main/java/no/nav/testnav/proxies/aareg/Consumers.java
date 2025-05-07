package no.nav.testnav.proxies.aareg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Configuration
@ConfigurationProperties(prefix = "consumers")
@NoArgsConstructor(access = PACKAGE)
@Setter(PRIVATE)
@Getter(PRIVATE)
public class Consumers {

    private static final String ENV = "{-env}";

    private ServerProperties aaregServices;
    private ServerProperties aaregVedlikehold;

    public ServerProperties getAaregServices(String env) {

        var environment = "q2".equals(env) ?  "" : "-%s".formatted(env);
        return ServerProperties.of(
                getAaregServices().getCluster(),
                getAaregServices().getNamespace(),
                getAaregServices().getName().replace(ENV, environment),
                getAaregServices().getUrl().replace(ENV, env)
        );
    }

    public ServerProperties getAaregVedlikehold(String env) {

        return ServerProperties.of(
                getAaregVedlikehold().getCluster(),
                getAaregVedlikehold().getNamespace(),
                getAaregVedlikehold().getName().replace(ENV, env),
                getAaregVedlikehold().getUrl().replace(ENV, env)
        );
    }
}
