package no.nav.dolly.libs.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveRequestContext implements WebFilter {

    private static final String CONTEXT_KEY = "requestContext";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return chain.filter(exchange)
                .contextWrite(Context.of(CONTEXT_KEY, exchange.getRequest()));
    }

    public static Mono<ServerHttpRequest> getContext() {

        return Mono.deferContextual(contextView -> Mono.just(contextView.get(CONTEXT_KEY)));
    }
}