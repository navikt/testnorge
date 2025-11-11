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
public class Fullmakt {

    private static final String CLUSTER = "dev-fss";
    private static final String NAMESPACE = "repr";
    private static final String NAME = "repr-fullmakt";

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build() {
        var authenticationFilter = authenticationFilterService
                .getFakedingsAuthenticationFilter(CLUSTER, NAMESPACE, NAME, targets.fullmakt);
        return spec -> spec
                .path("/fullmakt/**")
                .filters(f -> f
                        .stripPrefix(1)
                        .setResponseHeader("Content-Type", "application/json; charset=UTF-8")
                        .filter(authenticationFilter))
                .uri(targets.fullmakt);
    }

}
