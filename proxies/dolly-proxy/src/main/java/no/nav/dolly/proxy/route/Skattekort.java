package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Skattekort {

    private static final String CLUSTER = "dev-gcp";
    private static final String NAMESPACE = "okonomi";

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull SpecialCase env) {

        var uri = env == SpecialCase.Q1 ? targets.skattekortQ1 : targets.skattekortQ2;
        var authenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE, env.name, uri);

        return spec -> spec
                .path("/skattekort/%s/**".formatted(env.code))
                .filters(f -> f
                        .stripPrefix(1)
                        .rewritePath("/%s/(?<segment>.*)".formatted(env.code), "/${segment}")
                        .filter(authenticationFilter))
                .uri(uri);
    }

    @RequiredArgsConstructor
    enum SpecialCase {
        Q1("q1", "sokos-skattekort-q1"),
        Q2("q2", "sokos-skattekort");

        private final String code;
        private final String name;
    }
}
