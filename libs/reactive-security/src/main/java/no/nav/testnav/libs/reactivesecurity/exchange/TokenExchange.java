package no.nav.testnav.libs.reactivesecurity.exchange;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class TokenExchange implements ExchangeToken {

    private final GetAuthenticatedResourceServerType getAuthenticatedTypeAction;
    private final Map<ResourceServerType, ExchangeToken> exchanges;

    public TokenExchange(GetAuthenticatedResourceServerType getAuthenticatedTypeAction,
                         List<TokenService> tokenServices) {
        this.getAuthenticatedTypeAction = getAuthenticatedTypeAction;
        this.exchanges = new EnumMap<>(ResourceServerType.class);
        tokenServices.forEach(tokenService -> exchanges.put(tokenService.getType(), tokenService));
    }

    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        return getAuthenticatedTypeAction.call()
                .flatMap(type -> exchanges.get(type).exchange(serverProperties));
    }
}
