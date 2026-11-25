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
class EregDolly {

    private static final String CLUSTER = "dev-fss";
    private static final String NAMESPACE = "arbeidsforhold";
    private static final String NAME = "ereg-dolly-enheter-integrasjon-%s"; // Note replacement pattern.

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull SpecialCase env) {

        var url = targets.eregDolly.formatted(env.code);
        var name = NAME.formatted(env.code);
        var authenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE, name, url);

        return spec -> spec
                .path("/ereg-dolly/%s/**".formatted(env.code))
                .filters(f -> f
                        .stripPrefix(2)
                        .filter(authenticationFilter))
                .uri(url);

    }

    @RequiredArgsConstructor
    enum SpecialCase {
        Q1("q1"),
        Q2("q2"),
        Q4("q4");

        private final String code;
    }

}
