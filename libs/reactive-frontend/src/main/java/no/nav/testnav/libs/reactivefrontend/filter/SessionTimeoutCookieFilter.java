package no.nav.testnav.libs.reactivefrontend.filter;

import org.springframework.http.ResponseCookie;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class SessionTimeoutCookieFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().beforeCommit(() -> {
            var timeMillis = System.currentTimeMillis();
            return exchange.getSession().map(session -> timeMillis + session.getMaxIdleTime().toMillis()).map(
                    expiryTime -> ResponseCookie.from("sessionExpiry", expiryTime.toString()).path("/").sameSite("Lax").build()
            ).zipWith(Mono.just(
                    ResponseCookie.from("serverTime", String.valueOf(timeMillis)).path("/").sameSite("Lax").build()
            )).map(cookies -> {
                exchange.getResponse().addCookie(cookies.getT1());
                exchange.getResponse().addCookie(cookies.getT2());
                return exchange;
            }).then();
        });
        return chain.filter(exchange);
    }
}
