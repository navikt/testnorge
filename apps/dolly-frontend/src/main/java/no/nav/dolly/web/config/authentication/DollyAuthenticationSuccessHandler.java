package no.nav.dolly.web.config.authentication;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.config.csrf.SessionCsrfTokenRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Clock;

import static no.nav.dolly.web.config.session.SessionLifetimeFilter.AUTH_TIME;
import static no.nav.dolly.web.config.session.SessionLifetimeFilter.EXPIRED;
import static no.nav.dolly.web.config.session.SessionLifetimeFilter.LAST_USER_ACTIVITY;

@Slf4j
public class DollyAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final URI idPortenLocation = URI.create("/bruker");
    private final URI defaultLocation = URI.create("/");

    private final ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();
    private final Clock clock;

    public DollyAuthenticationSuccessHandler() {
        this(Clock.systemUTC());
    }

    DollyAuthenticationSuccessHandler(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        var requestPath = exchange.getRequest().getPath().toString();
        var location = requestPath.equals("/login/oauth2/code/idporten") ? idPortenLocation : defaultLocation;

        return exchange.getSession()
                .doOnNext(session -> {
                    var now = clock.instant();
                    session.getAttributes().remove(SessionCsrfTokenRepository.SESSION_ATTRIBUTE);
                    session.getAttributes().remove(EXPIRED);
                    session.getAttributes().put(AUTH_TIME, now);
                    session.getAttributes().put(LAST_USER_ACTIVITY, now);
                })
                .then(Mono.defer(() -> redirectStrategy.sendRedirect(exchange, location)));
    }
}
