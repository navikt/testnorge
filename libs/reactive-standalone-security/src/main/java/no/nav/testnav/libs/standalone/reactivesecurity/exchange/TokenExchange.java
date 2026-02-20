package no.nav.testnav.libs.standalone.reactivesecurity.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenExchange {

    private final AzureAdTokenService azureAdTokenService;
    private final ObjectMapper objectMapper;
    private final Map<String, AccessToken> tokenCache = new HashMap<>();

    public Mono<AccessToken> exchange(ServerProperties serverProperties) {

        var key = String.format(serverProperties.toAzureAdScope());

        if (!tokenCache.containsKey(key) ||
                expires(tokenCache.get(key))) {

            synchronized (this) {
                if (!tokenCache.containsKey(key) ||
                        expires(tokenCache.get(key))) {
                    return azureAdTokenService.exchange(serverProperties)
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

        return Instant.ofEpochSecond(objectMapper.readTree(body).get("exp").asInt())
                .minusSeconds(300)
                .isBefore(Instant.now());
    }
}
