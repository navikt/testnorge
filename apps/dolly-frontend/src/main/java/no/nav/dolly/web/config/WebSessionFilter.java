package no.nav.dolly.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
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
                .filter(this::sessionExpired)
                .map(WebSession::invalidate)
                .then(chain.filter(exchange));
    }

    private Boolean sessionExpired(WebSession session){
        for(String sid: webSessionConfig.getExpiredSIDs()){
            if (session.getId().equals(sid)) return true;
        }
        return false;
    }
}