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
class Dokarkiv {

    private static final String CLUSTER = "dev-fss";
    private static final String NAMESPACE = "teamdokumenthandtering";
    private static final String NAME = "dokarkiv%s"; // Note replacement pattern.

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build(SpecialCase env) {

        var url = targets.dokarkiv.formatted(env.nameAndUrlReplacement);
        var authenticationFilter = authenticationFilterService.getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE, NAME.formatted(env.nameAndUrlReplacement), url);
        return spec -> spec
                .path("/dokarkiv/%s/**".formatted(env.code))
                .filters(f -> f
                        .stripPrefix(2)
                        .filter(authenticationFilter))
                .uri(url);

    }

    @RequiredArgsConstructor
    enum SpecialCase {
        Q1("q1", "-q1"),
        Q2("q2", ""),
        Q4("q4", "-q4");

        private final String code;
        private final String nameAndUrlReplacement;
    }

}
