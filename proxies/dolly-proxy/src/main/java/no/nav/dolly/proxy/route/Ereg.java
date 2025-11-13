package no.nav.dolly.proxy.route;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class Ereg {

    private final Targets targets;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull SpecialCase env) {
        var miljo = env.getCode();
        var uri = targets.ereg.contains("%s") ? targets.ereg.formatted(miljo) : targets.ereg;
        return spec -> spec
                .path("/ereg/api/%s/**".formatted(miljo))
                .filters(f -> f
                        .stripPrefix(1) // Strip /ereg
                        .rewritePath("/api/%s/(?<segment>.*)".formatted(miljo), "/${segment}")
                        .removeRequestHeader(HttpHeaders.AUTHORIZATION))
                .uri(uri);
    }

    @RequiredArgsConstructor
    enum SpecialCase {
        Q1("q1"),
        Q2("q2"),
        Q4("q4");

        @Getter(AccessLevel.PRIVATE)
        private final String code;
    }

}