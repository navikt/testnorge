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
class Pdl {

    private static final String CLUSTER = "dev-fss";
    private static final String NAMESPACE = "pdl";

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull Pdl.SpecialCase env) {

        var url = switch (env) {
            case API -> targets.pdlApi;
            case API_Q1 -> targets.pdlApiQ1;
            case TESTDATA -> targets.pdlTestdata;
        };
        var authenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE, env.name, url);

        return switch (env) {

            case TESTDATA -> spec -> spec
                    .path("/pdl-testdata/**")
                    .filters(f -> f
                            .stripPrefix(1)
                            .filter(authenticationFilter))
                    .uri(url);

            case API -> spec -> spec
                    .path("/pdl-api/**")
                    .filters(f -> f
                            .stripPrefix(1)
                            .filter(authenticationFilter))
                    .uri(url);

            case API_Q1 -> spec -> spec
                    .path("/pdl-api-q1/**")
                    .filters(f -> f
                            .stripPrefix(1)
                            .filter(authenticationFilter))
                    .uri(url);

        };

    }

    @RequiredArgsConstructor
    enum SpecialCase {
        TESTDATA("pdl-testdata"),
        API("pdl-api"),
        API_Q1("pdl-api-q1");

        private final String name;
    }

}
