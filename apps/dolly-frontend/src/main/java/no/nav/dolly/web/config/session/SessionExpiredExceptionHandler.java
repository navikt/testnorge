package no.nav.dolly.web.config.session;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.service.SessionExpiredException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;

import static java.util.Objects.isNull;

@Slf4j
@Component
@Profile("idporten")
@Order(-2)
public class SessionExpiredExceptionHandler implements WebExceptionHandler {

    public static final String SESSION_HEADER = "X-Dolly-Session";
    public static final String SESSION_EXPIRED = "expired";
    private static final URI LOGIN_PAGE = URI.create("/login");
    private static final String CLIENT_LOG_PATH = "/api/dolly-logg";

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        var sessionExpired = ExceptionUtils.throwableOfType(ex, SessionExpiredException.class);
        if (isNull(sessionExpired) || exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        log.info("Sesjonen er utløpt: {}", sessionExpired.getMessage());
        return exchange
                .getSession()
                .flatMap(session -> isPageNavigation(exchange.getRequest())
                        ? session.invalidate().then(Mono.defer(() -> redirectToLogin(exchange.getResponse())))
                        : markExpiredAndRespond(exchange, session, sessionExpired));
    }

    private static Mono<Void> markExpiredAndRespond(ServerWebExchange exchange, WebSession session, SessionExpiredException ex) {
        var reason = ex instanceof SessionLifetimeFilter.LifetimeExpiredException lifetimeExpired
                ? lifetimeExpired.reason()
                : SessionLifetimeFilter.ExpiryReason.REFRESH_FAILED;
        SessionLifetimeFilter.markExpired(session, reason);
        return writeSessionExpired(exchange);
    }

    private static Mono<Void> redirectToLogin(ServerHttpResponse response) {
        response.getHeaders().set(SESSION_HEADER, SESSION_EXPIRED);
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(LOGIN_PAGE);
        return response.setComplete();
    }

    static Mono<Void> writeSessionExpired(ServerWebExchange exchange) {
        var response = exchange.getResponse();
        response.setStatusCode(isClientLog(exchange.getRequest()) ? HttpStatus.BAD_REQUEST : HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(SESSION_HEADER, SESSION_EXPIRED);
        return response.setComplete();
    }

    private static boolean isClientLog(ServerHttpRequest request) {
        return HttpMethod.POST.equals(request.getMethod()) && CLIENT_LOG_PATH.equals(request.getPath().value());
    }

    static boolean isPageNavigation(ServerHttpRequest request) {
        return HttpMethod.GET.equals(request.getMethod())
                && request.getHeaders().getAccept().stream()
                .anyMatch(mediaType -> mediaType.equalsTypeAndSubtype(MediaType.TEXT_HTML));
    }
}
