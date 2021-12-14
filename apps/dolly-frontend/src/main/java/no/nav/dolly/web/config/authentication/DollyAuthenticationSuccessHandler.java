package no.nav.dolly.web.config.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
public class DollyAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final URI idPortenLocation = URI.create("/bruker");
    private final URI defaultLocation = URI.create("/");

    private final ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        var requestPath = exchange.getRequest().getPath().toString();

        log.info("Request path: " + requestPath);
        if (requestPath.contains("idporten")){
            log.info("Redirecter til bruker-side");
            return redirectStrategy.sendRedirect(exchange, idPortenLocation);
        }else {
            log.info("Redirecter til dolly forside");
            return redirectStrategy.sendRedirect(exchange, defaultLocation);
        }
    }
}
