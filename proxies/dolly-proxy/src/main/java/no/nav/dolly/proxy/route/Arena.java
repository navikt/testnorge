package no.nav.dolly.proxy.route;

import lombok.AccessLevel;
import lombok.Getter;
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
                .path("/arena/%s/**".formatted(env.getCode()))
                .filters(f -> f
                        .stripPrefix(2))
                .uri(targets.arenaForvalteren.formatted(env.getCode()));
    }

    @RequiredArgsConstructor
    enum SpecialCase {
        Q1("q1"),
        Q2("q2"),
        Q4("q4");

        @Getter(AccessLevel.PACKAGE)
        private final String code;

    }

}
