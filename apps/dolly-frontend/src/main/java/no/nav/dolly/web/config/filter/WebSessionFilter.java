package no.nav.dolly.web.config.filter;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.web.config.WebSessionConfig;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;


@RequiredArgsConstructor
public class WebSessionFilter implements WebFilter {

    private final WebSessionConfig webSessionConfig;
    private final ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getSession()
                .flatMap(session -> Mono.zip(Mono.just(session), Mono.just(sessionExpired(session))))
                .flatMap(tuple -> {
                    if (Boolean.TRUE.equals(tuple.getT2())) {
                        return this.webSessionConfig
                                .removeExpiredSID(tuple.getT1().getId())
                                .then(redirectStrategy.sendRedirect(exchange, URI.create("/logout")));
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