package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import no.nav.dolly.proxy.config.Consumers;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Skattekort {

    private final Consumers consumers;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull SpecialCase env) {

        var serverProperties = switch (env) {
            case Q1 -> consumers.getSkattekortQ1();
            case Q2 -> consumers.getSkattekortQ2();
        };
        var authenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(serverProperties);

        return spec -> spec
                .path("/skattekort/%s/**".formatted(env.code))
                .filters(f -> f
                        .stripPrefix(1)
                        .rewritePath("/%s/(?<segment>.*)".formatted(env.code), "/${segment}")
                        .filter(authenticationFilter))
                .uri(serverProperties.getUrl());
    }

    @RequiredArgsConstructor
    enum SpecialCase {
        Q1("q1"),
        Q2("q2");

        private final String code;
    }
}
