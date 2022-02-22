package no.nav.testnav.libs.reactivefrontend.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import reactor.core.publisher.Mono;

public class AddAuthenticationHeaderToRequestGatewayFilterFactory extends AbstractGatewayFilterFactory<GenerateToken> {
    @Override
    public GatewayFilter apply(GenerateToken generateToken) {
        return (exchange, chain) -> generateToken
                .getToken(exchange)
//                .map(token -> exchange.getRequest().mutate().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build())
                .map(token -> exchange.getRequest().mutate().header("Auth2", "Bearer " + token).build())
                .flatMap(value -> chain.filter(exchange.mutate().request(value).build()))
                .switchIfEmpty(Mono.defer(() -> chain.filter(exchange)));
    }
}
