package no.nav.dolly.web.config.authentication;

import no.nav.dolly.web.config.csrf.SessionCsrfTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.server.WebFilterChain;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static no.nav.dolly.web.config.session.SessionLifetimeFilter.AUTH_TIME;
import static no.nav.dolly.web.config.session.SessionLifetimeFilter.EXPIRED;
import static no.nav.dolly.web.config.session.SessionLifetimeFilter.LAST_USER_ACTIVITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DollyAuthenticationSuccessHandlerTest {

    private static final Instant NOW = Instant.parse("2026-11-04T12:00:00Z");

    private final DollyAuthenticationSuccessHandler handler =
            new DollyAuthenticationSuccessHandler(Clock.fixed(NOW, ZoneOffset.UTC));
    private final MockServerWebExchange exchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/login/oauth2/code/idporten"));

    @Test
    void shouldRemoveCsrfTokenFromBeforeLogin() {
        var repository = new SessionCsrfTokenRepository();
        var tokenBeforeLogin = repository.generateToken(exchange).block();
        repository.saveToken(exchange, tokenBeforeLogin).block();

        new DollyAuthenticationSuccessHandler()
                .onAuthenticationSuccess(webFilterExchange(), mock(Authentication.class))
                .block();

        assertThat(repository.loadToken(exchange).blockOptional()).isEmpty();
        assertThat(exchange.getResponse().getHeaders().getFirst(HttpHeaders.LOCATION)).isEqualTo("/bruker");
    }

    @Test
    void shouldStartNewSessionLifetime() {
        var session = exchange.getSession().block();
        session.getAttributes().put(AUTH_TIME, NOW.minusSeconds(10_000));
        session.getAttributes().put(EXPIRED, "INACTIVITY");

        handler.onAuthenticationSuccess(webFilterExchange(), mock(Authentication.class)).block();

        assertThat((Instant) session.getAttribute(AUTH_TIME)).isEqualTo(NOW);
        assertThat((Instant) session.getAttribute(LAST_USER_ACTIVITY)).isEqualTo(NOW);
        assertThat(session.getAttributes()).doesNotContainKey(EXPIRED);
    }

    private WebFilterExchange webFilterExchange() {
        return new WebFilterExchange(exchange, mock(WebFilterChain.class));
    }
}
