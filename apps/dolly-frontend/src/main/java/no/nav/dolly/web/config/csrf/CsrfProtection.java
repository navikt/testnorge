package no.nav.dolly.web.config.csrf;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.csrf.CsrfWebFilter;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestHandler;
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CsrfProtection {

    public static final String CSRF_HEADER = "X-Dolly-Csrf";
    public static final String CSRF_INVALID = "invalid";
    private static final ServerWebExchangeMatcher LOGOUT_FORM = ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/logout");
    private static final ServerWebExchangeMatcher CLIENT_LOG = ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/api/dolly-logg");
    private static final URI LOGOUT_FAILED_LOCATION = URI.create("/");

    public static ServerHttpSecurity apply(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(new SessionCsrfTokenRepository())
                        .csrfTokenRequestHandler(new HeaderOnlyCsrfTokenRequestHandler())
                        .requireCsrfProtectionMatcher(CsrfWebFilter.DEFAULT_CSRF_MATCHER)
                        .accessDeniedHandler(CsrfProtection::reject));
    }

    static Mono<Void> reject(ServerWebExchange exchange, AccessDeniedException denied) {
        log.warn("Avviste {}-request: {}", exchange.getRequest().getMethod(), denied.getMessage());
        var response = exchange.getResponse();
        return LOGOUT_FORM.matches(exchange)
                .flatMap(logout -> {
                    if (logout.isMatch()) {
                        return redirect(response);
                    }
                    return CLIENT_LOG.matches(exchange)
                            .flatMap(clientLog -> clientLog.isMatch()
                                    ? reject(response, HttpStatus.BAD_REQUEST)
                                    : forbidden(response));
                });
    }

    static Mono<Void> forbidden(ServerHttpResponse response) {
        return reject(response, HttpStatus.FORBIDDEN);
    }

    private static Mono<Void> reject(ServerHttpResponse response, HttpStatus status) {
        response.setStatusCode(status);
        response.getHeaders().set(CSRF_HEADER, CSRF_INVALID);
        return response.setComplete();
    }

    private static Mono<Void> redirect(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(LOGOUT_FAILED_LOCATION);
        return response.setComplete();
    }

    static class HeaderOnlyCsrfTokenRequestHandler implements ServerCsrfTokenRequestHandler {

        private final XorServerCsrfTokenRequestAttributeHandler delegate = new XorServerCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(ServerWebExchange exchange, Mono<CsrfToken> csrfToken) {
            delegate.handle(exchange, csrfToken);
        }

        @Override
        public Mono<String> resolveCsrfTokenValue(ServerWebExchange exchange, CsrfToken csrfToken) {
            return LOGOUT_FORM.matches(exchange)
                    .flatMap(match -> delegate.resolveCsrfTokenValue(
                            match.isMatch() ? exchange : new WithoutFormData(exchange),
                            csrfToken));
        }
    }

    private static class WithoutFormData extends ServerWebExchangeDecorator {

        WithoutFormData(ServerWebExchange delegate) {
            super(delegate);
        }

        @Override
        public Mono<MultiValueMap<String, String>> getFormData() {
            return Mono.empty();
        }
    }
}
