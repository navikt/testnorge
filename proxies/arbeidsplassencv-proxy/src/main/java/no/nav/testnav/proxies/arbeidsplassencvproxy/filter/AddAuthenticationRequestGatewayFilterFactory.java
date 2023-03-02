package no.nav.testnav.proxies.arbeidsplassencvproxy.filter;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactiveproxy.filter.MonoRequestBuilder;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.ideporten.IdportenService;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.proxies.arbeidsplassencvproxy.consumer.FakedingsConsumer;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AddAuthenticationRequestGatewayFilterFactory extends AbstractGatewayFilterFactory<MonoRequestBuilder> {
    public static final String HEADER_NAV_CONSUMER_TOKEN = "Nav-Consumer-Token";

    public static GatewayFilter bearerAuthenticationHeaderFilter(Supplier<Mono<String>> getToken) {
        return new AddAuthenticationRequestGatewayFilterFactory().apply(builder -> {
            return getToken.get().map(token -> builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        });
    }

    public static GatewayFilter basicAuthAuthenticationHeaderFilter(String username, String password) {
        return new AddAuthenticationRequestGatewayFilterFactory().apply(builder -> {
            return Mono.just(builder.headers(headers -> headers.setBasicAuth(username, password)));
        });
    }

    public static GatewayFilter apiKeyAuthenticationHeaderFilter(String apiKey) {
        return new AddAuthenticationRequestGatewayFilterFactory().apply(builder -> {
            return Mono.just(builder.headers(headers -> builder.header(HttpHeaders.AUTHORIZATION, apiKey)));
        });
    }

    public static GatewayFilter bearerAuthenticationAndNavConsumerTokenHeaderFilter(Supplier<Mono<String>> getToken) {
        return new AddAuthenticationRequestGatewayFilterFactory().apply(builder -> {
            return getToken.get().map(token -> builder
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HEADER_NAV_CONSUMER_TOKEN, "Bearer " + token)
            );
        });
    }

    public static GatewayFilter bearerIdportenHeaderFilter(FakedingsConsumer fakedingsConsumer,
                                                           TokenXService tokenXService,
                                                           Supplier<Mono<String>> getToken) {

        return (exchange, chain) -> {
            var httpRequest = exchange.getRequest();
            var ident = httpRequest.getHeaders().getFirst("fnr");
            getToken.get()
                    .flatMap(token -> fakedingsConsumer.getFakeToken(ident)
                            .map(faketoken -> tokenXService.exchange(faketoken)
                                    .map(tokenX ->
                                            new AddAuthenticationRequestGatewayFilterFactory().apply(builder ->
                                                    Mono.just(builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenX)));
        }
    }

    @Override
    public GatewayFilter apply(MonoRequestBuilder changeRequest) {
        return (exchange, chain) -> changeRequest
                .build(exchange.getRequest().mutate())
                .flatMap(value -> chain.filter(exchange.mutate().request(value.build()).build()));
    }
}