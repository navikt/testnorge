package no.nav.testnav.libs.reactivefrontend.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@FunctionalInterface
public interface GenerateToken {
    Mono<String> getToken(ServerWebExchange exchange);
}
