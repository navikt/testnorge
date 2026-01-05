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
class Krrstub {

    private static final String CLUSTER = "dev-gcp";
    private static final String NAMESPACE = "team-rocket";
    private static final String NAME = "digdir-krr-stub";

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build() {

        var authenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE, NAME, targets.krrstub);

        return spec -> spec
                .path("/krrstub/api/v2/**")
                .filters(f -> f
                        .stripPrefix(1)
                        .filter(authenticationFilter))
                .uri(targets.krrstub);

    }

}
