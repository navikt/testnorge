package no.nav.testnav.libs.reactivefrontend.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;

public class AddAuthenticationHeaderToRequestGatewayFilterFactory extends AbstractGatewayFilterFactory<GenerateToken> {
    @Override
    public GatewayFilter apply(GenerateToken generateToken) {
        return (exchange, chain) -> generateToken
                .getToken(exchange)
                .map(token -> exchange.getRequest().mutate().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build())
                .flatMap(value -> chain.filter(exchange.mutate().request(value).build()));
    }
}
