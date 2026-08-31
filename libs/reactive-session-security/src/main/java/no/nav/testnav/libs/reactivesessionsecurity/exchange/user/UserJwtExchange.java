package no.nav.testnav.libs.reactivesessionsecurity.exchange.user;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenXExchange;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

@Slf4j
@Component
@Import(TestnavBrukerServiceProperties.class)
public class UserJwtExchange {
    private final WebClient webClient;
    private final TestnavBrukerServiceProperties serviceProperties;
    private final TokenXExchange tokenExchange;
    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, String> tokenCache;

    public UserJwtExchange(TestnavBrukerServiceProperties serviceProperties,
                           TokenXExchange tokenExchange,
                           ObjectMapper objectMapper) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.objectMapper = objectMapper;
        this.tokenCache = new ConcurrentHashMap<>();
    }

    public Mono<String> generateJwt(String id, ServerWebExchange exchange) {
        return generateJwt(id, () -> tokenExchange.exchange(serviceProperties, exchange)
                .flatMap(accessToken -> new GetTokenCommand(webClient, accessToken.getTokenValue(), id).call()));
    }

    public Mono<String> generateJwt(String id, String accessToken) {
        return generateJwt(id, () -> new GetTokenCommand(webClient, accessToken, id).call());
    }

    private Mono<String> generateJwt(String id, Supplier<Mono<String>> tokenSupplier) {
        if (!tokenCache.containsKey(id) || expires(tokenCache.get(id))) {
            synchronized (this) {
                if (!tokenCache.containsKey(id) || expires(tokenCache.get(id))) {
                    return tokenSupplier.get()
                            .doOnNext(token -> tokenCache.put(id, token));
                }
                return Mono.just(tokenCache.get(id));
            }
        }
        return Mono.just(tokenCache.get(id));
    }

    @SneakyThrows
    private boolean expires(String token) {

        var chunks = token.split("\\.");
        var body = new String(Base64.getDecoder().decode(chunks[1]));

        return Instant.ofEpochSecond(objectMapper.readTree(body).get("exp").asInt())
                .minusSeconds(300)
                .isBefore(Instant.now());
    }
}