package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Arena {

    private final Targets targets;

    Function<PredicateSpec, Buildable<Route>> build() {
        return spec -> spec
                .path("/arena/api/**")
                .filters(f -> f.stripPrefix(1))
                .uri(targets.arenaOrds);
    }

    Function<PredicateSpec, Buildable<Route>> build(SpecialCase env) {
        return spec -> spec
                .path("/arena/%s/**".formatted(env.code))
                .filters(f -> f
                        .stripPrefix(2))
                .uri(targets.arenaForvalteren.formatted(env.code));
    }

    @RequiredArgsConstructor
    enum SpecialCase {
        Q1("q1"),
        Q2("q2"),
        Q4("q4");

        private final String code;

    }

}
