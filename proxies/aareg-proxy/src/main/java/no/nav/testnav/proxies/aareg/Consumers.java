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
@Setter(PACKAGE)
@Getter(PRIVATE)
public class Consumers {

    private static final String ENV = "-{env}";
    private static final String DASH = "-%s";

    private ServerProperties aaregServices;
    private ServerProperties aaregVedlikehold;

    public ServerProperties getAaregServices(String env) {

        var environment = "q2".equals(env) ?  "" : DASH.formatted(env);
        return ServerProperties.of(
                getAaregServices().getCluster(),
                getAaregServices().getNamespace(),
                getAaregServices().getName().replace(ENV, environment),
                getAaregServices().getUrl().replace(ENV, DASH.formatted(env))
        );
    }

    public ServerProperties getAaregVedlikehold(String env) {

        return ServerProperties.of(
                getAaregVedlikehold().getCluster(),
                getAaregVedlikehold().getNamespace(),
                getAaregVedlikehold().getName().replace(ENV, DASH.formatted(env)),
                getAaregVedlikehold().getUrl().replace(ENV, DASH.formatted(env))
        );
    }
}
