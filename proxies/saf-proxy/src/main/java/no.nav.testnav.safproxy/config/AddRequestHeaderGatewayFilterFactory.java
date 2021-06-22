package no.nav.testnav.safproxy.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;

import no.nav.testnav.safproxy.config.GetHeader;

public class AddRequestHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<GetHeader> {
    @Override
    public GatewayFilter apply(GetHeader getHeader) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header(getHeader.getName(), getHeader.getValue())
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }
}