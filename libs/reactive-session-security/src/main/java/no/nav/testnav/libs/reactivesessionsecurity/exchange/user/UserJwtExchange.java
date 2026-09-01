package no.nav.testnav.libs.reactivesessionsecurity.exchange.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenXExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

@Component
@Import(TestnavBrukerServiceProperties.class)
public class UserJwtExchange {

    private static final Duration CACHE_EXPIRY_MARGIN = Duration.ofMinutes(5);

    private final WebClient webClient;
    private final TestnavBrukerServiceProperties serviceProperties;
    private final TokenXExchange tokenExchange;
    private final ConcurrentMap<String, String> tokenCache;
    private final ConcurrentMap<String, Mono<String>> tokenRequests;

    @Autowired
    public UserJwtExchange(TestnavBrukerServiceProperties serviceProperties,
                           TokenXExchange tokenExchange) {
        this(serviceProperties, tokenExchange, WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build());
    }

    @Deprecated(forRemoval = false)
    public UserJwtExchange(TestnavBrukerServiceProperties serviceProperties,
                           TokenXExchange tokenExchange,
                           ObjectMapper objectMapper) {
        this(serviceProperties, tokenExchange);
    }

    UserJwtExchange(TestnavBrukerServiceProperties serviceProperties,
                    TokenXExchange tokenExchange,
                    WebClient webClient) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = webClient;
        this.tokenCache = new ConcurrentHashMap<>();
        this.tokenRequests = new ConcurrentHashMap<>();
    }

    public Mono<String> generateJwt(String id, ServerWebExchange exchange) {
        return generateJwt(id, () -> tokenExchange.exchange(serviceProperties, exchange)
                .flatMap(accessToken -> new GetTokenCommand(webClient, accessToken.getTokenValue(), id).call()));
    }

    public Mono<String> generateJwtWithAccessToken(String id, String accessToken) {
        return generateJwt(id, () -> new GetTokenCommand(webClient, accessToken, id).call());
    }

    private Mono<String> generateJwt(String id, Supplier<Mono<String>> tokenSupplier) {
        return Mono.defer(() -> {
            var cachedToken = tokenCache.get(id);
            if (nonNull(cachedToken) && !expires(cachedToken)) {
                return Mono.just(cachedToken);
            }

            if (nonNull(cachedToken)) {
                tokenCache.remove(id, cachedToken);
            }

            return tokenRequests.computeIfAbsent(id, userId -> Mono.defer(tokenSupplier)
                    .doOnNext(token -> cacheToken(userId, token))
                    .doFinally(_ -> tokenRequests.remove(userId))
                    .cache());
        });
    }

    private void cacheToken(String id, String token) {
        if (expires(token)) {
            throw new JWTDecodeException("User-Jwt er utløpt eller utløper for snart.");
        }
        tokenCache.put(id, token);
    }

    private boolean expires(String token) {
        var expiresAt = JWT.decode(token).getExpiresAtAsInstant();
        if (expiresAt == null) {
            throw new JWTDecodeException("User-Jwt mangler utløpstid.");
        }
        return expiresAt
                .minus(CACHE_EXPIRY_MARGIN)
                .isBefore(Instant.now());
    }
}