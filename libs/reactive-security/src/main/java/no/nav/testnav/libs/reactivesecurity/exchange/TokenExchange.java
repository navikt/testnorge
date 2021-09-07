package no.nav.testnav.libs.reactivesecurity.exchange;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.service.AuthenticationTokenResolver;


@Service
public class TokenExchange {
    private final AuthenticationTokenResolver tokenResolver;

    private final Map<String, GenerateTokenExchange> exchanges = new HashMap<>();

    public TokenExchange(AuthenticationTokenResolver tokenResolver) {
        this.tokenResolver = tokenResolver;
    }

    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        return tokenResolver
                .getClientRegistrationId()
                .flatMap(id -> exchanges.get(id).generateToken(serverProperties));
    }

    public void addExchange(String id, GenerateTokenExchange exchange) {
        exchanges.put(id, exchange);
    }
}
