package no.nav.registre.testnorge.profil.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AzureOnBehalfOfTokenService {

    private final GetAuthenticatedToken getAuthenticatedToken;
    private final TexasClient texasClient;

    public Mono<AccessToken> exchange(String scope) {
        return getAuthenticatedToken.call()
                .flatMap(token -> texasClient.exchange(TexasClient.ENTRA_ID, scope, token.getAccessTokenValue()));
    }

}
