package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class Sigrunstub {

    private final Targets targets;

    Function<PredicateSpec, Buildable<Route>> build() {
        return spec -> spec
                .path("/sigrunstub/**")
                .filters(f -> f.stripPrefix(1))
                .uri(targets.sigrunstub);
    }

}
