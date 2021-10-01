package no.nav.testnav.libs.reactivesecurity.exchange;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerType;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerTypeAction;


@Service
@RequiredArgsConstructor
public class TokenExchange {

    private final GetAuthenticatedResourceServerTypeAction getAuthenticatedTypeAction;

    private final Map<ResourceServerType, GenerateTokenExchange> exchanges = new HashMap<>();

    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        return getAuthenticatedTypeAction.call()
                .flatMap(type -> exchanges.get(type).generateToken(serverProperties));
    }

    public void addExchange(ResourceServerType type, GenerateTokenExchange exchange) {
        exchanges.put(type, exchange);
    }

}
