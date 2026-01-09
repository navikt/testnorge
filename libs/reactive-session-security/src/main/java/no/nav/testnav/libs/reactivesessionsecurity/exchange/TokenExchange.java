package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TokenExchange implements ExchangeToken {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().build();

    private final ClientRegistrationIdResolver clientRegistrationIdResolver;
    private final Map<ResourceServerType, ExchangeToken> exchanges;
    private final Map<String, AccessToken> tokenCache;

    public TokenExchange(ClientRegistrationIdResolver clientRegistrationIdResolver) {
        this.clientRegistrationIdResolver = clientRegistrationIdResolver;
        this.exchanges = new HashMap<>();
        this.tokenCache = new HashMap<>();
    }

    public void addExchange(ResourceServerType id, ExchangeToken exchange) {
        exchanges.put(id, exchange);
    }

    public Mono<AccessToken> exchange(ServerProperties serverProperties, ServerWebExchange exchange) {

        return Mono.zip(ReactiveSecurityContextHolder.getContext().map(this::getUser),
                        clientRegistrationIdResolver.getClientRegistrationId())
                .flatMap(data -> Mono.just(String.format("%s:%s", data.getT1(), serverProperties.getScope(data.getT2())))
                        .flatMap(key -> {
                            if (!tokenCache.containsKey(key) ||
                                    expires(tokenCache.get(key))) {

                                synchronized (this) {
                                    if (!tokenCache.containsKey(key) ||
                                            expires(tokenCache.get(key))) {
                                        return exchanges.get(data.getT2()).exchange(serverProperties, exchange)
                                                .doOnNext(token ->
                                                        tokenCache.put(key, token));
                                    } else {

                                        return Mono.just(tokenCache.get(key));
                                    }
                                }

                            } else {

                                return Mono.just(tokenCache.get(key));
                            }
                        }));
    }

    private String getUser(SecurityContext context) {

        return ((OAuth2AuthenticationToken) context.getAuthentication()).getPrincipal().getName();
    }

    @SneakyThrows
    private boolean expires(AccessToken accessToken) {

        var chunks = accessToken.getTokenValue().split("\\.");
        var body = Base64.getDecoder().decode(chunks[1]);

        return Instant.ofEpochSecond(OBJECT_MAPPER.readTree(body).get("exp").asInt())
                .minusSeconds(300)
                .isBefore(Instant.now());
    }
}

