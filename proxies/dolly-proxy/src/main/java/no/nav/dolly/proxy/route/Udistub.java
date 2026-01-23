package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Component
@RequiredArgsConstructor
class Udistub {

    private static final String CLUSTER = "dev-fss";
    private static final String NAME = "testnav-udi-stub";
    private static final String NAMESPACE = "dolly";

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build() {

        var authenticationFilter = authenticationFilterService
                .getNavAuthenticationFilter(CLUSTER, NAMESPACE, NAME, targets.udistub);

        return spec -> spec
                .path("/udistub/**")
                .filters(f -> f
                        .stripPrefix(1)
                        .setResponseHeader(CONTENT_TYPE, "application/json; charset=UTF-8")
                        .filter(authenticationFilter))
                .uri(targets.udistub);

    }

}
