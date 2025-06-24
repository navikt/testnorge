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

    private static final String ALTINN3_CONSUMER = "testnav-altinn3-tilgang-service";

    private final Texas texas;

    public Function<PredicateSpec, Buildable<Route>> buildOpenApiRoute() {
        var altinn3Url = getUrl();
        return spec -> spec
                .path("/altinn3/v3/api-docs")
                .filters(f -> f.stripPrefix(1))
                .uri(altinn3Url);
    }

    public Function<PredicateSpec, Buildable<Route>> buildApiRoute() {
        var altinn3Url = getUrl();
        var altinn3Token = texas.getToken(ALTINN3_CONSUMER);
        var altinn3Filter = new BearerTokenGatewayFilter(altinn3Token);
        return spec -> spec
                .order(2)
                .path("/altinn3/**")
                .filters(f -> f.stripPrefix(1).filter(altinn3Filter))
                .uri(altinn3Url);
    }

    private String getUrl() {
        return texas
                .consumer(ALTINN3_CONSUMER)
                .orElseThrow(() -> new IllegalStateException("Expected consumer " + ALTINN3_CONSUMER + " not found"))
                .getUrl();
    }

}
