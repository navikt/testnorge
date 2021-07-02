package no.nav.testnav.libs.frontend.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

import no.nav.testnav.libs.frontend.domain.GetHeader;

public class AddRequestHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<Mono<GetHeader>> {
    @Override
    public GatewayFilter apply(Mono<GetHeader> getHeader) {
        return (exchange, chain) -> getHeader
                .map(value -> exchange.getRequest().mutate().header(value.getName(), value.getValue()).build())
                .flatMap(value -> chain.filter(exchange.mutate().request(value).build()));
    }

    public static GatewayFilter createAuthenticationHeaderFilter(Supplier<Mono<String>> tokenService) {
        return new AddRequestHeaderGatewayFilterFactory().apply(
                tokenService.get().map(token -> new GetHeader(() -> HttpHeaders.AUTHORIZATION, () -> "Bearer " + token))
        );
    }
}