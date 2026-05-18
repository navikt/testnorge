package no.nav.testnav.libs.servletsecurity.exchange;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedResourceServerType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TokenExchange implements ExchangeToken {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().build();

    private final GetAuthenticatedResourceServerType getAuthenticatedTypeAction;
    private final Map<ResourceServerType, ExchangeToken> exchanges = new HashMap<>();
    private final Map<String, AccessToken> tokenCache = new HashMap<>();

    public TokenExchange(GetAuthenticatedResourceServerType getAuthenticatedTypeAction, List<TokenService> tokenServices) {
        this.getAuthenticatedTypeAction = getAuthenticatedTypeAction;
        tokenServices.forEach(tokenService -> exchanges.put(tokenService.getType(), tokenService));
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {

        var type = getAuthenticatedTypeAction.call();
        var user = ReactiveSecurityContextHolder.getContext().map(this::getUser);

        var key = String.format("%s:%s", user, serverProperties.getScope(type));

        if (!tokenCache.containsKey(key) ||
                expires(tokenCache.get(key))) {

            synchronized (this) {
                if (!tokenCache.containsKey(key) ||
                        expires(tokenCache.get(key))) {
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
    private boolean expires(AccessToken accessToken) {

        var chunks = accessToken.getTokenValue().split("\\.");
        var body = Base64.getDecoder().decode(chunks[1]);

        return Instant.ofEpochSecond(OBJECT_MAPPER.readTree(body).get("exp").asInt())
                .minusSeconds(300)
                .isBefore(Instant.now());
    }

    private String getUser(SecurityContext context) {

        return ((OAuth2AuthenticationToken) context.getAuthentication()).getPrincipal().getName();
    }
}
