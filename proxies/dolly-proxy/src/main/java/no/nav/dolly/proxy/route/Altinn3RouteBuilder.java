package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class Altinn3RouteBuilder {

    private final Consumers consumers;
    private final TokenExchange tokenExchange;

    public Function<PredicateSpec, Buildable<Route>> buildOpenApiRoute() {
        return spec -> spec
                .path("/altinn3/v3/api-docs")
                .filters(f -> f.stripPrefix(1))
                .uri(consumers.getAltinn3TilgangService().getUrl());
    }

    public Function<PredicateSpec, Buildable<Route>> buildApiRoute() {
        return spec -> spec
                .path("/altinn3/**")
                .and()
                .not(not -> not.path("/internal/**"))
                .filters(f -> f
                        .stripPrefix(1)
                        .filter(AddAuthenticationRequestGatewayFilterFactory
                                .bearerAuthenticationHeaderFilter(
                                        () -> tokenExchange
                                                .exchange(consumers.getAltinn3TilgangService())
                                                .map(AccessToken::getTokenValue))))
                .uri(consumers.getAltinn3TilgangService().getUrl());
    }

}
