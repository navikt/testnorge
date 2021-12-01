package no.nav.testnav.libs.reactivesecurity.exchange;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.reactivesecurity.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Service
public class TokenExchange {

    private final GetAuthenticatedResourceServerType getAuthenticatedTypeAction;
    private final Map<ResourceServerType, GenerateToken> exchanges = new HashMap<>();

    public TokenExchange(GetAuthenticatedResourceServerType getAuthenticatedTypeAction, List<TokenService> tokenServices) {
        this.getAuthenticatedTypeAction = getAuthenticatedTypeAction;
        tokenServices.forEach(tokenService -> exchanges.put(tokenService.getType(), tokenService));
    }

    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        return getAuthenticatedTypeAction.call()
                .flatMap(type -> exchanges.get(type).generateToken(serverProperties));
    }
}
