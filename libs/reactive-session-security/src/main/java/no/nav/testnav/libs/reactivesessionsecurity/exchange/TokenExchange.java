package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivesessionsecurity.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TokenExchange implements GenerateTokenExchange {
    private final ClientRegistrationIdResolver clientRegistrationIdResolver;

    private final Map<String, GenerateTokenExchange> exchanges = new HashMap<>();

    @Override
    public Mono<AccessToken> generateToken(ServerProperties serverProperties, ServerWebExchange exchange) {
        return clientRegistrationIdResolver
                .getClientRegistrationId()
                .flatMap(id -> getExchange(id).generateToken(serverProperties, exchange));
    }

    public void addExchange(String id, GenerateTokenExchange exchange) {
        exchanges.put(id, exchange);
    }

    private GenerateTokenExchange getExchange(String id) {
        if (!exchanges.containsKey(id)) {
            throw new NoSuchElementException("Finner ikke exchange for id " + id + ".");
        }
        return exchanges.get(id);
    }

}

