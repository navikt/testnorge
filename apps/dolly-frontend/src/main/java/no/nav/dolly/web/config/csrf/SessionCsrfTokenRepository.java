package no.nav.dolly.web.config.csrf;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.csrf.DefaultCsrfToken;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.util.Objects.isNull;

/**
 * Bytter ikke sesjons-ID ved lagring, slik at parallelle kall med den gamle session-cookien beholder session.
 */
public class SessionCsrfTokenRepository implements ServerCsrfTokenRepository {

    public static final String SESSION_ATTRIBUTE = SessionCsrfTokenRepository.class.getName() + ".CSRF_TOKEN";
    public static final String HEADER_NAME = "X-CSRF-TOKEN";
    public static final String PARAMETER_NAME = "_csrf";

    @Override
    public Mono<CsrfToken> generateToken(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> new DefaultCsrfToken(HEADER_NAME, PARAMETER_NAME, UUID.randomUUID().toString()));
    }

    @Override
    public Mono<Void> saveToken(ServerWebExchange exchange, CsrfToken token) {
        return exchange.getSession()
                .doOnNext(session -> {
                    if (isNull(token)) {
                        session.getAttributes().remove(SESSION_ATTRIBUTE);
                    } else {
                        session.getAttributes().put(SESSION_ATTRIBUTE, token);
                    }
                })
                .then();
    }

    @Override
    public Mono<CsrfToken> loadToken(ServerWebExchange exchange) {
        return exchange.getSession()
                .mapNotNull(session -> session.getAttribute(SESSION_ATTRIBUTE));
    }
}
