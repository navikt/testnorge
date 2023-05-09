package no.nav.testnav.libs.servletsecurity.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedId;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedResourceServerType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TokenExchange implements ExchangeToken {

    private final GetAuthenticatedResourceServerType getAuthenticatedTypeAction;
    private final Map<ResourceServerType, ExchangeToken> exchanges = new HashMap<>();
    private final Map<String, AccessToken> tokenCache = new HashMap<>();
    private final ObjectMapper objectMapper;
    private final GetAuthenticatedId getAuthenticatedId;

    public TokenExchange(GetAuthenticatedResourceServerType getAuthenticatedTypeAction, List<TokenService> tokenServices,
                         ObjectMapper objectMapper, GetAuthenticatedId getAuthenticatedId) {
        this.getAuthenticatedTypeAction = getAuthenticatedTypeAction;
        tokenServices.forEach(tokenService -> exchanges.put(tokenService.getType(), tokenService));
        this.objectMapper = objectMapper;
        this.getAuthenticatedId = getAuthenticatedId;
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {

        var type = getAuthenticatedTypeAction.call();
        var oid = getAuthenticatedId.call();
        log.info("AuthenticatedId: {}", oid);

        var key = String.format("%s:%s", oid, serverProperties.getScope(type));

        if (!tokenCache.containsKey(key) ||
                expires(tokenCache.get(key))
                        .minusSeconds(300)
                        .isBefore(Instant.now())) {

            synchronized (this) {
                if (!tokenCache.containsKey(key) ||
                        expires(tokenCache.get(key))
                                .minusSeconds(300)
                                .isBefore(Instant.now())) {
                    return exchanges.get(type).exchange(serverProperties)
                            .doOnNext(token ->
                                    tokenCache.put(key, token));
                } else {

                    return Mono.just(tokenCache.get(key));
                }
            }

        } else {

            return Mono.just(tokenCache.get(key));
        }
    }

    @SneakyThrows
    private Instant expires(AccessToken accessToken) {

        var chunks = accessToken.getTokenValue().split("\\.");
        var body = new String(Base64.getDecoder().decode(chunks[1]));

        return Instant.ofEpochSecond(objectMapper.readTree(body).get("exp").asInt());
    }
}
