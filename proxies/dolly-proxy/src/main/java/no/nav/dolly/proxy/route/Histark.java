package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Histark {

    private final Targets targets;

    Function<PredicateSpec, Buildable<Route>> build() {
        return spec -> spec
                .path("/histark/**")
                .filters(f -> f.stripPrefix(1))
                .uri(targets.histark);
    }

}
