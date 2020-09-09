package no.nav.registre.testnorge.libs.oauth2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;


@Service
@RequiredArgsConstructor
public class AccessTokenService {
    private final AuthenticationTokenResolver authenticationTokenResolver;
    private final ClientCredentialGenerateAccessTokenService clientCredentialGenerateAccessTokenService;

    public AccessToken generateToken(ClientCredential clientCredential) {
        if (authenticationTokenResolver.isClientCredentials()) {
            return clientCredentialGenerateAccessTokenService.generateToken(clientCredential);
        } else {
            //TODO Legg til støtte for OAuth 2.0 On-Behalf-Of
            throw new UnsupportedOperationException("OAuth 2.0 On-Behalf-Of flow not supported");
        }
    }

}
