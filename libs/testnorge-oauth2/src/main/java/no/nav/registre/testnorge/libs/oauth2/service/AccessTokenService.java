package no.nav.registre.testnorge.libs.oauth2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;


@Service
@RequiredArgsConstructor
public class AccessTokenService {
    private final AuthenticationTokenResolver authenticationTokenResolver;
    private final ClientCredentialGenerateAccessTokenService clientCredentialGenerateAccessTokenService;
    private final OnBehalfOfGenerateAccessTokenService onBehalfOfGenerateAccessTokenService;

    public AccessToken generateToken(ClientCredential clientCredential, AccessScopes accessScopes) {
        if (authenticationTokenResolver.isClientCredentials()) {
            return clientCredentialGenerateAccessTokenService.generateToken(clientCredential, accessScopes);
        } else {
            return onBehalfOfGenerateAccessTokenService.generateToken(clientCredential, accessScopes);
        }
    }

}
