package no.nav.testnav.libs.reactivefrontend.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
public class IgnoreSetCookieFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().beforeCommit(() -> {
            if (exchange.getResponse().getHeaders().containsKey("set-cookie")) {
                log.warn("Fjerner 'set-cookie' fra remote response siden vi ønsker at frontend skal håndtere cookies selv, ikke fra en remote app.");
                exchange.getResponse().getHeaders().remove("set-cookie");
            }
            return Mono.empty();
        });
        return chain.filter(exchange);
    }
}
