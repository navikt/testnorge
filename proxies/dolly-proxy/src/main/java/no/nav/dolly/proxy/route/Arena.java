package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.config.Consumers;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Arena {

    private final Consumers consumers;

    Function<PredicateSpec, Buildable<Route>> build() {
        return spec -> spec
                .path("/arena/api/**")
                .filters(f -> f.stripPrefix(1))
                .uri(consumers.getArenaForvalteren().getUrl());
    }

    Function<PredicateSpec, Buildable<Route>> build(@NonNull SpecialCase env) {
        var url = switch (env) {
            case Q1 -> consumers.getArenaOrdsQ1().getUrl();
            case Q2 -> consumers.getArenaOrdsQ2().getUrl();
            case Q4 -> consumers.getArenaOrdsQ4().getUrl();
        };
        return spec -> spec
                .path("/arena/%s/**".formatted(env.code))
                .filters(f -> f
                        .stripPrefix(2))
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
