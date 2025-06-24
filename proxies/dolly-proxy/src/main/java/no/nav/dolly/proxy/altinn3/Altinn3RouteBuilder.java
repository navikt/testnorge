package no.nav.dolly.proxy.altinn3;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.texas.Texas;
import no.nav.dolly.proxy.BearerTokenGatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class Altinn3RouteBuilder {

    private static final String ALTINN3_CONSUMER = "testnav-altinn3-tilgang-service-prod";

    private final Texas texas;

    public Function<PredicateSpec, Buildable<Route>> build() {

        var altinn3Token = texas.get(ALTINN3_CONSUMER);
        var altinn3Url = texas.consumer(ALTINN3_CONSUMER).orElseThrow(() -> new IllegalStateException("Expected consumer " + ALTINN3_CONSUMER + " not found")).getUrl();
        var altinn3Filter = new BearerTokenGatewayFilter(altinn3Token);
        return spec -> spec
                .path("/altinn3/**")
                .and()
                .path("/internal/**").negate()
                .filters(filterSpec -> filterSpec.filter(altinn3Filter))
                .uri(altinn3Url);

    }

}
