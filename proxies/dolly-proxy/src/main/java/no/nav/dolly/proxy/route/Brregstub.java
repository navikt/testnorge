package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.config.Consumers;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Brregstub {

    private final Consumers consumers;

    Function<PredicateSpec, Buildable<Route>> build() {
        return spec -> spec
                .path("/brregstub/**")
                .filters(f -> f.stripPrefix(1))
                .uri(consumers.getBrregstub().getUrl());
    }

}
