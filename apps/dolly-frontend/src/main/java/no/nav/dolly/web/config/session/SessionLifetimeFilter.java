package no.nav.dolly.web.config.session;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.service.SessionExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.WebSessionIdResolver;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@Profile("idporten")
@Order(SessionLifetimeFilter.ORDER)
public class SessionLifetimeFilter implements WebFilter {

    static final int ORDER = -101;
    public static final String AUTH_TIME = "dolly.session.authTime";
    public static final String LAST_USER_ACTIVITY = "dolly.session.lastUserActivity";
    public static final String BACKGROUND_HEADER = "X-Dolly-Background";
    public static final String EXPIRED = "dolly.session.expired";

    private static final String SECURITY_CONTEXT =
            WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;
    private static final ServerWebExchangeMatcher EXCLUDED_PATHS = ServerWebExchangeMatchers.pathMatchers(
            "/login",
            "/login/**",
            "/logout",
            "/oauth2/**",
            "/internal/**",
            "/*.css",
            "/*.js",
            "/*.mjs",
            "/*.png",
            "/assets/*",
            "/favicon.ico");

    enum ExpiryReason {
        MISSING_AUTH_TIME,
        MAX_LIFETIME,
        INACTIVITY,
        REFRESH_FAILED,
        UNKNOWN_SESSION
    }

    static class LifetimeExpiredException extends SessionExpiredException {

        private final transient ExpiryReason reason;

        LifetimeExpiredException(ExpiryReason reason) {
            super("Sesjonen er utløpt: " + reason);
            this.reason = reason;
        }

        ExpiryReason reason() {
            return reason;
        }
    }

    private final Duration inactivityTimeout;
    private final Duration maxLifetime;
    private final WebSessionIdResolver sessionIdResolver;
    private final Clock clock;

    @Autowired
    public SessionLifetimeFilter(
            @Value("${spring.session.timeout:30m}") String inactivityTimeout,
            @Value("${dolly.session.max-lifetime:2h}") String maxLifetime,
            WebSessionIdResolver sessionIdResolver
    ) {
        this(DurationStyle.detectAndParse(inactivityTimeout, ChronoUnit.SECONDS),
                DurationStyle.detectAndParse(maxLifetime, ChronoUnit.SECONDS),
                sessionIdResolver, Clock.systemUTC());
    }

    SessionLifetimeFilter(Duration inactivityTimeout, Duration maxLifetime, WebSessionIdResolver sessionIdResolver, Clock clock) {
        this.inactivityTimeout = inactivityTimeout;
        this.maxLifetime = maxLifetime;
        this.sessionIdResolver = sessionIdResolver;
        this.clock = clock;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return EXCLUDED_PATHS
                .matches(exchange)
                .flatMap(match -> match.isMatch()
                        ? chain.filter(exchange)
                        : exchange.getSession().flatMap(session -> checkSession(exchange, session, chain)));
    }

    private Mono<Void> checkSession(ServerWebExchange exchange, WebSession session, WebFilterChain chain) {
        var request = exchange.getRequest();
        if (isUnknownSession(exchange, session) && !SessionExpiredExceptionHandler.isPageNavigation(request)) {
            markExpired(session, ExpiryReason.UNKNOWN_SESSION);
            return SessionExpiredExceptionHandler.writeSessionExpired(exchange);
        }
        var now = clock.instant();
        var expiryReason = expiryReason(session, now);
        if (expiryReason.isPresent()) {
            return Mono.error(new LifetimeExpiredException(expiryReason.get()));
        }
        if (isAuthenticated(session) && isUserActivity(request)) {
            session.getAttributes().put(LAST_USER_ACTIVITY, now);
        }
        return chain.filter(exchange);
    }

    static void markExpired(WebSession session, ExpiryReason reason) {
        if (nonNull(session.getAttribute(EXPIRED))) {
            return;
        }
        log.info("Sesjonen er utløpt, årsak: {}", reason);
        var attributes = session.getAttributes();
        List.copyOf(attributes.keySet()).forEach(attributes::remove);
        attributes.put(EXPIRED, reason.name());
    }

    Optional<ExpiryReason> expiryReason(WebSession session, Instant now) {
        String expired = session.getAttribute(EXPIRED);
        if (nonNull(expired)) {
            return Optional.of(ExpiryReason.valueOf(expired));
        }
        if (isNull(session.getAttribute(SECURITY_CONTEXT))) {
            return Optional.empty();
        }
        Instant authTime = session.getAttribute(AUTH_TIME);
        if (isNull(authTime)) {
            return Optional.of(ExpiryReason.MISSING_AUTH_TIME);
        }
        if (!now.isBefore(authTime.plus(maxLifetime))) {
            return Optional.of(ExpiryReason.MAX_LIFETIME);
        }
        Instant lastUserActivity = session.getAttributeOrDefault(LAST_USER_ACTIVITY, authTime);
        if (!now.isBefore(lastUserActivity.plus(inactivityTimeout))) {
            return Optional.of(ExpiryReason.INACTIVITY);
        }
        return Optional.empty();
    }

    private boolean isUnknownSession(ServerWebExchange exchange, WebSession session) {
        return !session.isStarted() && !sessionIdResolver.resolveSessionIds(exchange).isEmpty();
    }

    private static boolean isAuthenticated(WebSession session) {
        return nonNull(session.getAttribute(SECURITY_CONTEXT));
    }

    private static boolean isUserActivity(ServerHttpRequest request) {
        return !"true".equalsIgnoreCase(request.getHeaders().getFirst(BACKGROUND_HEADER))
                && request.getHeaders().getAccept().stream()
                .noneMatch(mediaType -> mediaType.equalsTypeAndSubtype(MediaType.TEXT_EVENT_STREAM));
    }
}
