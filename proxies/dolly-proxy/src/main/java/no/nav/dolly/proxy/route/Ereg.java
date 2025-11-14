package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class Ereg {

    private final Targets targets;

    Function<PredicateSpec, Buildable<Route>> build(String miljo) {
        var uri = targets.ereg.contains("%s") ? targets.ereg.formatted(miljo) : targets.ereg;
        return spec -> spec
                .path("/ereg/api/" + miljo + "/**")
                .filters(f -> f
                        .stripPrefix(1) // Strip /ereg
                        .rewritePath("/api/" + miljo + "/(?<segment>.*)", "/${segment}")
                        .removeRequestHeader(HttpHeaders.AUTHORIZATION))
                .uri(uri);
    }

}