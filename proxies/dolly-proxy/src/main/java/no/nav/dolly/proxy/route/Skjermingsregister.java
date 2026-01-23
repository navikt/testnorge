package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Skjermingsregister {

    private static final String CLUSTER = "dev-fss";
    private static final String NAME = "skjermede-personer";
    private static final String NAMESPACE = "nom";

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build() {

        var authenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE, NAME, targets.skjermingsregister);

        return spec -> spec
                .path("/skjermingsregister/**")
                .filters(f -> f
                        .stripPrefix(1)
                        .filter(authenticationFilter))
                .uri(targets.skjermingsregister);

    }

}
