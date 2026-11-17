package no.nav.testnav.libs.reactivesecurity.exchange.tokenx;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty("spring.security.oauth2.resourceserver.tokenx.issuer-uri")
@RequiredArgsConstructor
public class TokenXService implements TokenService {

    private final GetAuthenticatedToken getAuthenticatedTokenAction;
    private final TexasClient texasClient;

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        return getAuthenticatedTokenAction.call()
                .flatMap(token -> exchange(serverProperties, token.getAccessTokenValue()));
    }

    public Mono<AccessToken> exchange(ServerProperties serverProperties, String userToken) {
        return texasClient.exchange(TexasClient.TOKENX, serverProperties.toTokenXScope(), userToken);
    }

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.TOKEN_X;
    }

}
