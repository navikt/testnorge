package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Service
@RequiredArgsConstructor
public class TokenExchange implements ExchangeToken {
    private final ClientRegistrationIdResolver clientRegistrationIdResolver;

    private final Map<String, ExchangeToken> exchanges = new HashMap<>();

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties, ServerWebExchange exchange) {
        return clientRegistrationIdResolver
                .getClientRegistrationId()
                .flatMap(id -> getExchange(id).exchange(serverProperties, exchange));
    }

    public void addExchange(String id, ExchangeToken exchange) {
        exchanges.put(id, exchange);
    }

    private ExchangeToken getExchange(String id) {
        if (!exchanges.containsKey(id)) {
            throw new NoSuchElementException("Finner ikke exchange for id " + id + ".");
        }
        return exchanges.get(id);
    }

}

