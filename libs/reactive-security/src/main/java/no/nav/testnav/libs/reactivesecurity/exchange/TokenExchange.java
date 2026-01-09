package no.nav.testnav.libs.reactivesecurity.exchange;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TokenExchange implements ExchangeToken {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().build();

    private final GetAuthenticatedResourceServerType getAuthenticatedTypeAction;
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final Map<ResourceServerType, ExchangeToken> exchanges;
    private final Map<String, AccessToken> tokenCache;

    public TokenExchange(GetAuthenticatedResourceServerType getAuthenticatedTypeAction,
                         GetAuthenticatedUserId getAuthenticatedUserId,
                         List<TokenService> tokenServices) {
        this.getAuthenticatedTypeAction = getAuthenticatedTypeAction;
        this.getAuthenticatedUserId = getAuthenticatedUserId;
        this.exchanges = new EnumMap<>(ResourceServerType.class);
        this.tokenCache = new HashMap<>();
        tokenServices.forEach(tokenService -> exchanges.put(tokenService.getType(), tokenService));
    }

    public Mono<AccessToken> exchange(ServerProperties serverProperties) {

        return Mono.zip(getAuthenticatedUserId.call(), getAuthenticatedTypeAction.call())
                .flatMap(data -> Mono.just(String.format("%s:%s", data.getT1(), serverProperties.getScope(data.getT2())))
                        .flatMap(key -> {

                            if (!tokenCache.containsKey(key) ||
                                    expires(tokenCache.get(key))) {

                                synchronized (this) {
                                    if (!tokenCache.containsKey(key) ||
                                            expires(tokenCache.get(key))) {
                                        return exchanges.get(data.getT2()).exchange(serverProperties)
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

    @SneakyThrows
    private boolean expires(AccessToken accessToken) {

        var chunks = accessToken.getTokenValue().split("\\.");
        var body = Base64.getDecoder().decode(chunks[1]);

        return Instant.ofEpochSecond(OBJECT_MAPPER.readTree(body).get("exp").asInt())
                .minusSeconds(300)
                .isBefore(Instant.now());
    }
}
