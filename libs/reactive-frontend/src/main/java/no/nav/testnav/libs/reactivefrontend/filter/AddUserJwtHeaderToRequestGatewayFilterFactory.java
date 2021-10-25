package no.nav.testnav.libs.reactivefrontend.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.securitycore.config.UserConstant;

public class AddUserJwtHeaderToRequestGatewayFilterFactory extends AbstractGatewayFilterFactory<GenerateToken> {
    @Override
    public GatewayFilter apply(GenerateToken generateToken) {
        return (exchange, chain) -> generateToken
                .getToken(exchange)
                .map(token -> exchange.getRequest().mutate().header(UserConstant.USER_HEADER_JWT, token).build())
                .flatMap(value -> chain.filter(exchange.mutate().request(value).build()))
                .switchIfEmpty(Mono.defer(() -> chain.filter(exchange)));
    }
}
