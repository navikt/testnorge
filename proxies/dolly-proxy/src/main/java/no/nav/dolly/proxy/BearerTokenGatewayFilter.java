package no.nav.dolly.proxy;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.texas.TexasToken;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BearerTokenGatewayFilter implements GatewayFilter {

    private final Mono<TexasToken> token;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return token.flatMap(texasToken -> {
            var request = exchange
                    .getRequest()
                    .mutate()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + texasToken.access_token())
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        });

    }
}
