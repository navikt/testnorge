package no.nav.testnav.libs.frontend.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class IgnoreSetCookieFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().beforeCommit(() -> {

            if (exchange.getResponse().getHeaders().containsKey("set-cookie")) {
                exchange.getResponse().getHeaders().remove("set-cookie");
                log.warn("Fjerner 'set-cookie' fra remote response siden vi ønsker at frontend skal håntere cookies selv, ikke fra en remote app.");
            }

            return Mono.empty();
        });
        return chain.filter(exchange);
    }
}
