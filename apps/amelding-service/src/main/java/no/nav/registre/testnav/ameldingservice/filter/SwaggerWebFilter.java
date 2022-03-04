package no.nav.registre.testnav.ameldingservice.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SwaggerWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getURI().getPath().equals("/swagger")) {
            return chain
                    .filter(exchange.mutate()
                            .request(exchange.getRequest()
                                    .mutate().path("/swagger-ui.html").build())
                            .build());
        }

        return chain.filter(exchange);
    }
}