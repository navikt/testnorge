package no.nav.testnav.libs.frontend.filter;

import org.springframework.http.ResponseCookie;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class SessionTimeoutCookieFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var timeMillis = System.currentTimeMillis();
        return exchange.getSession().map(session -> timeMillis + session.getMaxIdleTime().toMillis()).map(
                expiryTime -> ResponseCookie.from("sessionExpiry", expiryTime.toString()).build()
        ).zipWith(Mono.just(
                ResponseCookie.from("serverTime", String.valueOf(timeMillis)).build()
        )).map(cookies -> {
            exchange.getResponse().addCookie(cookies.getT1());
            exchange.getResponse().addCookie(cookies.getT2());
            return exchange;
        }).flatMap(chain::filter);
    }
}
