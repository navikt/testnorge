package no.nav.testnav.libs.servletsecurity.exchange;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.servletsecurity.domain.ResourceServerType;


@Service
public class TokenExchange {

    private final GetAuthenticatedResourceServerType getAuthenticatedTypeAction;
    private final Map<ResourceServerType, GenerateToken> exchanges = new HashMap<>();

    public TokenExchange(GetAuthenticatedResourceServerType getAuthenticatedTypeAction, List<TokenService> tokenServices) {
        this.getAuthenticatedTypeAction = getAuthenticatedTypeAction;
        tokenServices.forEach(tokenService -> exchanges.put(tokenService.getType(), tokenService));
    }

    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        var type = getAuthenticatedTypeAction.call();
        return exchanges.get(type).generateToken(serverProperties);
    }
}
