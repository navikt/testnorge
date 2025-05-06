package no.nav.testnav.proxies.aareg;

import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PACKAGE;

@Configuration
@ConfigurationProperties(prefix = "consumers")
@NoArgsConstructor(access = PACKAGE)
@Setter(PACKAGE)
public class Consumers {

    private static final String ENV = "{-env}";

    private ServerProperties aaregServices;
    private ServerProperties aaregVedlikehold;

    public ServerProperties getAaregServices(String env) {

        var environment = "q2".equals(env) ?  "" : "-%s".formatted(env);
        return ServerProperties.of(
                aaregServices.getCluster(),
                aaregServices.getNamespace(),
                aaregServices.getName().replace(ENV, environment),
                aaregServices.getUrl().replace(ENV, env)
        );
    }

    public ServerProperties getAaregVedlikehold(String env) {

        return ServerProperties.of(
                aaregVedlikehold.getCluster(),
                aaregVedlikehold.getNamespace(),
                aaregVedlikehold.getName().replace(ENV, env),
                aaregVedlikehold.getUrl().replace(ENV, env)
        );
    }
}
