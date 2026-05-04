package no.nav.testnav.libs.reactivesessionsecurity.exchange.user;

import tools.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenXExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Import(TestnavBrukerServiceProperties.class)
public class UserJwtExchange {
    private final WebClient webClient;
    private final TestnavBrukerServiceProperties serviceProperties;
    private final TokenXExchange tokenExchange;
    private final ObjectMapper objectMapper;
    private final Map<String, String> tokenCache;

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
        this.tokenCache = new HashMap<>();
    }

    public Mono<String> generateJwt(String id, ServerWebExchange exchange) {

        if (!tokenCache.containsKey(id) || expires(tokenCache.get(id))) {

            synchronized (this) {
                if (!tokenCache.containsKey(id) || expires(tokenCache.get(id))) {
                    return tokenExchange.exchange(serviceProperties, exchange)
                            .flatMap(accessToken -> new GetTokenCommand(webClient, accessToken.getTokenValue(), id).call())
                            .doOnNext(token ->
                                    tokenCache.put(id, token));
                } else {

                    return Mono.just(tokenCache.get(id));
                }
            }

        } else {

            return Mono.just(tokenCache.get(id));
        }
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