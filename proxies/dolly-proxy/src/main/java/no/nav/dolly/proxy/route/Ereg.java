package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.config.Consumers;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Ereg {

    private final Consumers consumers;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull SpecialCase env) {

        var uri = switch (env) {
            case Q1 -> consumers.getEregQ1().getUrl();
            case Q2 -> consumers.getEregQ2().getUrl();
            case Q4 -> consumers.getEregQ4().getUrl();
        };

        return spec -> spec
                .path("/ereg/api/%s/**".formatted(env.code))
                .filters(f -> f
                        .stripPrefix(1) // Strip /ereg
                        .rewritePath("/api/%s/(?<segment>.*)".formatted(env.code), "/${segment}")
                        .removeRequestHeader(HttpHeaders.AUTHORIZATION))
                .uri(uri);

    }

    @RequiredArgsConstructor
    enum SpecialCase {
        Q1("q1"),
        Q2("q2"),
        Q4("q4");

        private final String code;
    }

}
