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
class Bistandsbehov {

    private static final String CLUSTER = "dev-gcp";
    private static final String NAMESPACE = "obo";
    private static final String NAME = "veilarbvedtaksstotte";

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build() {

        var bearerAuthenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE,
                        NAME, targets.getBistandsbehov());

        return spec -> spec
                .path("/bistandsbehov/**")
                .filters(f -> f.stripPrefix(1)
                        .filter(bearerAuthenticationFilter))
                .uri(targets.getBistandsbehov());
    }
}