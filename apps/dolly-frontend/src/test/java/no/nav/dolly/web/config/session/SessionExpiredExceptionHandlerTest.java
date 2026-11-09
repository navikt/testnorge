package no.nav.dolly.web.config.session;

import no.nav.dolly.web.service.SessionExpiredException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class SessionExpiredExceptionHandlerTest {

    private final SessionExpiredExceptionHandler handler = new SessionExpiredExceptionHandler();

    @Test
    void shouldMarkSessionExpiredWithoutInvalidatingForApiRequest() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker")
                .header(SessionLifetimeFilter.BACKGROUND_HEADER, "true"));
        var session = exchange.getSession().block();
        session.getAttributes().put("noe", "verdi");

        StepVerifier.create(handler.handle(exchange, new RuntimeException(new SessionExpiredException("utløpt"))))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exchange.getResponse().getHeaders().getFirst(SessionExpiredExceptionHandler.SESSION_HEADER))
                .isEqualTo(SessionExpiredExceptionHandler.SESSION_EXPIRED);
        assertThat(session.isExpired()).isFalse();
        assertThat(session.getAttributes()).containsOnlyKeys(SessionLifetimeFilter.EXPIRED);
        assertThat((String) session.getAttribute(SessionLifetimeFilter.EXPIRED)).isEqualTo("REFRESH_FAILED");
    }

    @Test
    void shouldRedirectPageNavigationToLogin() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/gruppe")
                .accept(MediaType.TEXT_HTML, MediaType.ALL));

        var session = exchange.getSession().block();
        session.getAttributes().put("noe", "verdi");

        StepVerifier.create(handler.handle(exchange, new SessionExpiredException("utløpt")))
                .verifyComplete();

        assertThat(session.isExpired()).isTrue();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(exchange.getResponse().getHeaders().getLocation()).hasToString("/login");
    }

    @Test
    void shouldReturnUnauthorizedForFetchWithWildcardAccept() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker")
                .accept(MediaType.APPLICATION_JSON, MediaType.ALL));

        StepVerifier.create(handler.handle(exchange, new SessionExpiredException("utløpt")))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldAnswerBadRequestForClientLog() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/dolly-logg"));

        StepVerifier.create(handler.handle(exchange, new SessionExpiredException("utløpt")))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exchange.getResponse().getHeaders().getFirst(SessionExpiredExceptionHandler.SESSION_HEADER))
                .isEqualTo(SessionExpiredExceptionHandler.SESSION_EXPIRED);
    }

    @Test
    void shouldPassOnOtherErrors() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        var error = new IllegalStateException("annen feil");

        StepVerifier.create(handler.handle(exchange, error))
                .expectErrorMatches(error::equals)
                .verify();
    }
}
