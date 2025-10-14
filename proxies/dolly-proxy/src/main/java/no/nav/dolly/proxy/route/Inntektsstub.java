package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Inntektsstub {

    private final Consumers consumers;

    Function<PredicateSpec, Buildable<Route>> build() {
        return spec -> spec
                .path("/inntektstub-proxy/**")
                .and()
                .not(not -> not.path("/internal/**"))
                .filters(f -> f.stripPrefix(1))
                .uri(consumers.inntektsstub.getUrl());
    }

}
