package no.nav.testnav.libs.servletsecurity.exchange;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenExchange {

    private final GetAuthenticatedResourceServerType getAuthenticatedResourceServerType;
    private final GetAuthenticatedToken getAuthenticatedToken;
    private final TexasClient texasClient;

    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        var token = getAuthenticatedToken.call();
        return switch (getAuthenticatedResourceServerType.call()) {
            case AZURE_AD -> token.isClientCredentials()
                    ? texasClient.token(TexasClient.ENTRA_ID, serverProperties.toAzureAdScope())
                    : texasClient.exchange(TexasClient.ENTRA_ID, serverProperties.toAzureAdScope(), token.getAccessTokenValue());
            case TOKEN_X -> texasClient.exchange(TexasClient.TOKENX, serverProperties.toTokenXScope(), token.getAccessTokenValue());
        };
    }
}
