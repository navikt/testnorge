package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import no.nav.testnav.libs.reactivesessionsecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesessionsecurity.domain.ServerProperties;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;

@Service
@RequiredArgsConstructor
@Primary
public class TokenExchange implements GenerateTokenExchange {
    private final ClientRegistrationIdResolver clientRegistrationIdService;

    private final Map<String, GenerateTokenExchange> exchanges = new HashMap<>();

    @Override
    public Mono<AccessToken> generateToken(ServerProperties serverProperties, ServerWebExchange exchange) {
        return clientRegistrationIdService
                .getClientRegistrationId()
                .flatMap(id -> exchanges.get(id).generateToken(serverProperties, exchange));
    }

    public void addExchange(String id, GenerateTokenExchange exchange) {
        exchanges.put(id, exchange);
    }

}

