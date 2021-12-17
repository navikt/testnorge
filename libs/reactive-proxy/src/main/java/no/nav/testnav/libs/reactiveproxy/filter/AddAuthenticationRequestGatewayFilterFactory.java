package no.nav.testnav.libs.reactiveproxy.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

public class AddAuthenticationRequestGatewayFilterFactory extends AbstractGatewayFilterFactory<MonoRequestBuilder> {
    public static final String HEADER_NAV_CONSUMER_TOKEN = "Nav-Consumer-Token";

    public static GatewayFilter createAuthenticationHeaderFilter(Supplier<Mono<String>> getToken) {
        return new AddAuthenticationRequestGatewayFilterFactory().apply((builder) -> {
            return getToken.get().map(token -> builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        });
    }

    public static GatewayFilter createBasicAuthenticationHeaderFilter(String username, String password) {
        return new AddAuthenticationRequestGatewayFilterFactory().apply((builder) -> {
            return Mono.just(builder.headers(headers -> headers.setBasicAuth(username, password)));
        });
    }

    public static GatewayFilter createAuthenticationAndNavConsumerTokenHeaderFilter(Supplier<Mono<String>> getToken) {
        return new AddAuthenticationRequestGatewayFilterFactory().apply((builder) -> {
            return getToken.get().map(token -> builder
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HEADER_NAV_CONSUMER_TOKEN, "Bearer " + token)
            );
        });
    }

    @Override
    public GatewayFilter apply(MonoRequestBuilder changeRequest) {
        return (exchange, chain) -> changeRequest
                .build(exchange.getRequest().mutate())
                .flatMap(value -> chain.filter(exchange.mutate().request(value.build()).build()));
    }
}