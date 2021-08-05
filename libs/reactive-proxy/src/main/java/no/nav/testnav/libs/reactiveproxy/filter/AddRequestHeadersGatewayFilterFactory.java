package no.nav.testnav.libs.reactiveproxy.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import java.util.Arrays;

public class AddRequestHeadersGatewayFilterFactory extends AbstractGatewayFilterFactory<GetHeader[]> {
    @Override
    public GatewayFilter apply(GetHeader... getHeaders) {
        return (exchange, chain) -> {
            var metatableRequest = exchange.getRequest().mutate();
            Arrays.stream(getHeaders)
                    .forEach(getHeader -> metatableRequest.header(getHeader.getName(), getHeader.getValue()));
            return chain.filter(exchange.mutate().request(metatableRequest.build()).build());
        };
    }
}