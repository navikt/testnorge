package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.TokenResolver;
import no.nav.testnav.libs.securitycore.command.tokenx.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.tokenx.TokenXProperties;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Import({
        TokenXProperties.class
})
public class TokenXExchange implements ExchangeToken {
    private final TokenResolver tokenService;
    private final WebClient webClient;
    private final TokenXProperties tokenX;
    private final ObjectMapper objectMapper;
    private final Map<String, AccessToken> tokenCache;

    TokenXExchange(TokenXProperties tokenX, TokenResolver tokenService, ObjectMapper objectMapper) {
        this.webClient = WebClient.builder().build();
        this.tokenX = tokenX;
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
        this.tokenCache = new HashMap<>();
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties, ServerWebExchange exchange) {

        return ReactiveSecurityContextHolder.getContext()
                .map(this::getUser)
                .map(user -> String.format("%s:%s", user, serverProperties.toTokenXScope()))
                .flatMap(key -> {
                    if (!tokenCache.containsKey(key) ||
                            expires(tokenCache.get(key))) {

                        synchronized (this) {
                            if (!tokenCache.containsKey(key) ||
                                    expires(tokenCache.get(key))) {

                                return tokenService
                                        .getToken(exchange)
                                        .flatMap(token -> new OnBehalfOfExchangeCommand(
                                                webClient,
                                                tokenX,
                                                serverProperties.toTokenXScope(),
                                                token.getAccessTokenValue()
                                        ).call())
                                        .doOnNext(token -> tokenCache.put(key, token));
                            } else {

                                return Mono.just(tokenCache.get(key));
                            }
                        }
                    } else {

                        return Mono.just(tokenCache.get(key));
                    }
                });
    }

    private String getUser(SecurityContext context) {

        return ((OAuth2AuthenticationToken) context.getAuthentication()).getPrincipal().getName();
    }

    @SneakyThrows
    private boolean expires(AccessToken accessToken) {

        var chunks = accessToken.getTokenValue().split("\\.");
        var body = new String(Base64.getDecoder().decode(chunks[1]));

        return Instant.ofEpochSecond(objectMapper.readTree(body).get("exp").asInt())
                .minusSeconds(300)
                .isBefore(Instant.now());
    }
}
