package no.nav.dolly.web.config.session;

import no.nav.dolly.web.service.SessionExpiredException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.mock.web.server.MockWebSession;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicBoolean;

import static no.nav.dolly.web.config.session.SessionLifetimeFilter.AUTH_TIME;
import static no.nav.dolly.web.config.session.SessionLifetimeFilter.BACKGROUND_HEADER;
import static no.nav.dolly.web.config.session.SessionLifetimeFilter.LAST_USER_ACTIVITY;
import static org.assertj.core.api.Assertions.assertThat;

class SessionLifetimeFilterTest {

    private static final Instant NOW = Instant.parse("2026-11-04T12:00:00Z");
    private static final Duration INACTIVITY = Duration.ofMinutes(30);
    private static final Duration MAX_LIFETIME = Duration.ofHours(2);
    private static final String SECURITY_CONTEXT =
            WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;

    private final SessionLifetimeFilter filter =
            new SessionLifetimeFilter(INACTIVITY, MAX_LIFETIME, new CookieWebSessionIdResolver(), Clock.fixed(NOW, ZoneOffset.UTC));
    private final SessionExpiredExceptionHandler handler = new SessionExpiredExceptionHandler();

    @Test
    void shouldUpdateLastUserActivityForUserRequest() {
        var exchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker"));
        var session = loggedInSession(exchange, NOW.minus(Duration.ofMinutes(10)), NOW.minus(Duration.ofMinutes(5)));
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(exchange, chain(chainCalled))).verifyComplete();

        assertThat(chainCalled).isTrue();
        assertThat((Instant) session.getAttribute(LAST_USER_ACTIVITY)).isEqualTo(NOW);
    }

    @Test
    void shouldNotUpdateLastUserActivityForBackgroundRequest() {
        var lastActivity = NOW.minus(Duration.ofMinutes(5));
        var exchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/infostripe")
                .header(BACKGROUND_HEADER, "true"));
        var session = loggedInSession(exchange, NOW.minus(Duration.ofMinutes(10)), lastActivity);
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(exchange, chain(chainCalled))).verifyComplete();

        assertThat(chainCalled).isTrue();
        assertThat((Instant) session.getAttribute(LAST_USER_ACTIVITY)).isEqualTo(lastActivity);
    }

    @Test
    void shouldExpireSessionAfterInactivityOnUserRequest() {
        var exchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker"));
        loggedInSession(exchange, NOW.minus(Duration.ofMinutes(40)), NOW.minus(INACTIVITY));
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(exchange, chain(chainCalled)))
                .expectErrorMatches(error -> error instanceof SessionExpiredException
                        && error.getMessage().contains("INACTIVITY"))
                .verify();

        assertThat(chainCalled).isFalse();
    }

    @Test
    void shouldClearSessionAndKeepExpiryForNextUserRequestWhenPollingAfterInactivity() {
        var session = new MockWebSession();
        var pollExchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/infostripe")
                .header(BACKGROUND_HEADER, "true"), session);
        session.getAttributes().put(SECURITY_CONTEXT, new SecurityContextImpl());
        session.getAttributes().put(AUTH_TIME, NOW.minus(Duration.ofMinutes(40)));
        session.getAttributes().put(LAST_USER_ACTIVITY, NOW.minus(INACTIVITY));
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(pollExchange, chain(chainCalled))
                        .onErrorResume(error -> handler.handle(pollExchange, error)))
                .verifyComplete();

        assertThat(chainCalled).isFalse();
        assertMarkedUnauthorized(pollExchange);
        assertThat(session.isExpired()).isFalse();
        assertThat(session.getAttributes()).containsOnlyKeys(SessionLifetimeFilter.EXPIRED);

        var userExchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker"), session);
        StepVerifier.create(filter.filter(userExchange, chain(chainCalled)))
                .expectErrorMatches(error -> error instanceof SessionExpiredException
                        && error.getMessage().contains("INACTIVITY"))
                .verify();
        assertThat(chainCalled).isFalse();
    }

    @Test
    void shouldReturnMarkerForEveryApiRequestAfterSessionIsMarkedExpired() {
        var session = new MockWebSession();
        session.getAttributes().put(SECURITY_CONTEXT, new SecurityContextImpl());
        session.getAttributes().put(AUTH_TIME, NOW.minus(MAX_LIFETIME));

        for (var attempt = 0; attempt < 3; attempt++) {
            var exchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/gruppe"), session);
            StepVerifier.create(filter.filter(exchange, chain(new AtomicBoolean()))
                            .onErrorResume(error -> handler.handle(exchange, error)))
                    .verifyComplete();
            assertMarkedUnauthorized(exchange);
        }
        assertThat(session.isExpired()).isFalse();
        assertThat((String) session.getAttribute(SessionLifetimeFilter.EXPIRED)).isEqualTo("MAX_LIFETIME");
    }

    @Test
    void shouldReturnMarkerForApiRequestWithUnknownSessionCookie() {
        var exchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker")
                .cookie(new HttpCookie("SESSION", "slettet-sesjon")));
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(exchange, chain(chainCalled))).verifyComplete();

        assertThat(chainCalled).isFalse();
        assertMarkedUnauthorized(exchange);
        var session = exchange.getSession().block();
        assertThat(session.isStarted()).isTrue();
        assertThat(session.getAttributes()).containsOnlyKeys(SessionLifetimeFilter.EXPIRED);
        assertThat((String) session.getAttribute(SessionLifetimeFilter.EXPIRED)).isEqualTo("UNKNOWN_SESSION");

        var nextExchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker"), session);
        StepVerifier.create(filter.filter(nextExchange, chain(chainCalled))
                        .onErrorResume(error -> handler.handle(nextExchange, error)))
                .verifyComplete();
        assertMarkedUnauthorized(nextExchange);
        assertThat(chainCalled).isFalse();
    }

    @Test
    void shouldNotCountEventStreamAsUserActivity() {
        var lastActivity = NOW.minus(Duration.ofMinutes(5));
        var exchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/bestilling/1/stream")
                .accept(MediaType.TEXT_EVENT_STREAM));
        var session = loggedInSession(exchange, NOW.minus(Duration.ofMinutes(10)), lastActivity);
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(exchange, chain(chainCalled))).verifyComplete();

        assertThat(chainCalled).isTrue();
        assertThat((Instant) session.getAttribute(LAST_USER_ACTIVITY)).isEqualTo(lastActivity);
    }

    @Test
    void shouldLetPageNavigationAndStaticFilesThroughWithUnknownSessionCookie() {
        var navigation = exchange(MockServerHttpRequest.get("/gruppe")
                .accept(MediaType.TEXT_HTML, MediaType.ALL)
                .cookie(new HttpCookie("SESSION", "slettet-sesjon")));
        var script = exchange(MockServerHttpRequest.get("/assets/index.js")
                .cookie(new HttpCookie("SESSION", "slettet-sesjon")));
        var navigationChainCalled = new AtomicBoolean();
        var scriptChainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(navigation, chain(navigationChainCalled))).verifyComplete();
        StepVerifier.create(filter.filter(script, chain(scriptChainCalled))).verifyComplete();

        assertThat(navigationChainCalled).isTrue();
        assertThat(scriptChainCalled).isTrue();
    }

    @Test
    void shouldLetRequestWithoutSessionCookieThrough() {
        var exchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker"));
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(exchange, chain(chainCalled))).verifyComplete();

        assertThat(chainCalled).isTrue();
    }

    @Test
    void shouldNotExpireLoggedOutSessionWithOldAuthTime() {
        var exchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/infostripe"));
        var session = exchange.getSession().block();
        session.getAttributes().put(AUTH_TIME, NOW.minus(Duration.ofHours(3)));
        session.getAttributes().put(LAST_USER_ACTIVITY, NOW.minus(Duration.ofHours(3)));
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(exchange, chain(chainCalled))).verifyComplete();

        assertThat(chainCalled).isTrue();
        assertThat((Instant) session.getAttribute(LAST_USER_ACTIVITY)).isEqualTo(NOW.minus(Duration.ofHours(3)));
        assertThat(session.getAttributes()).doesNotContainKey(SessionLifetimeFilter.EXPIRED);
    }

    @Test
    void shouldExpireSessionAfterMaxLifetimeEvenWhenActive() {
        var exchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker"));
        loggedInSession(exchange, NOW.minus(MAX_LIFETIME), NOW.minus(Duration.ofMinutes(1)));
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(exchange, chain(chainCalled)))
                .expectErrorMatches(error -> error instanceof SessionExpiredException
                        && error.getMessage().contains("MAX_LIFETIME"))
                .verify();

        assertThat(chainCalled).isFalse();
    }

    @Test
    void shouldReadUnitlessDurationsAsSeconds() {
        var configuredFilter = new SessionLifetimeFilter("1800", "7200", new CookieWebSessionIdResolver());
        var now = Instant.now();
        var exchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker"));
        loggedInSession(exchange, now.minus(Duration.ofMinutes(10)), now.minus(Duration.ofMinutes(5)));
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(configuredFilter.filter(exchange, chain(chainCalled))).verifyComplete();

        assertThat(chainCalled).isTrue();
    }

    @Test
    void shouldExpireAuthenticatedSessionWithoutAuthTime() {
        var exchange = exchange(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker"));
        exchange.getSession().block().getAttributes().put(SECURITY_CONTEXT, new SecurityContextImpl());

        StepVerifier.create(filter.filter(exchange, chain(new AtomicBoolean())))
                .expectErrorMatches(error -> error instanceof SessionExpiredException
                        && error.getMessage().contains("MISSING_AUTH_TIME"))
                .verify();
    }

    @Test
    void shouldLetAnonymousRequestsThroughWithoutTouchingSession() {
        var exchange = exchange(MockServerHttpRequest.get("/login"));
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(exchange, chain(chainCalled))).verifyComplete();

        assertThat(chainCalled).isTrue();
        assertThat(exchange.getSession().block().getAttributes()).isEmpty();
    }

    @Test
    void shouldLetLogoutThroughForExpiredSession() {
        var exchange = exchange(MockServerHttpRequest.get("/logout"));
        loggedInSession(exchange, NOW.minus(Duration.ofHours(3)), NOW.minus(Duration.ofHours(3)));
        var chainCalled = new AtomicBoolean();

        StepVerifier.create(filter.filter(exchange, chain(chainCalled))).verifyComplete();

        assertThat(chainCalled).isTrue();
    }

    private static void assertMarkedUnauthorized(MockServerWebExchange exchange) {
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exchange.getResponse().getHeaders().getFirst(SessionExpiredExceptionHandler.SESSION_HEADER))
                .isEqualTo(SessionExpiredExceptionHandler.SESSION_EXPIRED);
    }

    private static MockServerWebExchange exchange(MockServerHttpRequest.BaseBuilder<?> request) {
        return MockServerWebExchange.from(request);
    }

    private static MockServerWebExchange exchange(MockServerHttpRequest.BaseBuilder<?> request, WebSession session) {
        return MockServerWebExchange.builder(request.build()).session(session).build();
    }

    private static WebSession loggedInSession(MockServerWebExchange exchange, Instant authTime, Instant lastActivity) {
        var session = exchange.getSession().block();
        session.getAttributes().put(SECURITY_CONTEXT, new SecurityContextImpl());
        session.getAttributes().put(AUTH_TIME, authTime);
        session.getAttributes().put(LAST_USER_ACTIVITY, lastActivity);
        return session;
    }

    private static WebFilterChain chain(AtomicBoolean called) {
        return exchange -> {
            called.set(true);
            return Mono.empty();
        };
    }
}
