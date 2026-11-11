package no.nav.dolly.web.config.csrf;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import static org.assertj.core.api.Assertions.assertThat;

class SessionCsrfTokenRepositoryTest {

    private final SessionCsrfTokenRepository repository = new SessionCsrfTokenRepository();

    @Test
    void shouldKeepSessionIdWhenTokenIsSaved() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/session/csrf"));
        var sessionId = exchange.getSession().block().getId();

        repository.saveToken(exchange, repository.generateToken(exchange).block()).block();

        assertThat(exchange.getSession().block().getId()).isEqualTo(sessionId);
    }

    @Test
    void shouldLoadSavedTokenAndRemoveItOnNull() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/session/csrf"));
        var token = repository.generateToken(exchange).block();

        repository.saveToken(exchange, token).block();
        assertThat(repository.loadToken(exchange).block()).isEqualTo(token);

        repository.saveToken(exchange, null).block();
        assertThat(repository.loadToken(exchange).blockOptional()).isEmpty();
    }

    @Test
    void shouldGenerateUniqueTokens() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/session/csrf"));

        assertThat(repository.generateToken(exchange).block().getToken())
                .isNotEqualTo(repository.generateToken(exchange).block().getToken());
    }
}
