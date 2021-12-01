package no.nav.dolly.web.config.filter;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.web.config.WebSessionConfig;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;


@Component
@Order(1)
@RequiredArgsConstructor
public class WebSessionFilter implements WebFilter {

    private final WebSessionConfig webSessionConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getSession()
                .flatMap(session -> Mono.zip(Mono.just(session), Mono.just(sessionExpired(session))))
                .flatMap(tuple -> {
                    if (tuple.getT2()) {
                        ServerHttpResponse response = exchange.getResponse();
                        response.getCookies().remove("JSESSIONID");
                        response.getCookies().remove("sessionExpiry");
                        response.getCookies().remove("serverTime");

                        return this.webSessionConfig
                                .removeExpiredSID(tuple.getT1().getId())
                                .then(tuple.getT1().invalidate());
                    } else {
                        return chain.filter(exchange);
                    }
                });
    }

    private Boolean sessionExpired(WebSession session) {
        for (String sid : webSessionConfig.getExpiredSIDs()) {
            if (session.getId().equals(sid)) return true;
        }
        return false;
    }
}