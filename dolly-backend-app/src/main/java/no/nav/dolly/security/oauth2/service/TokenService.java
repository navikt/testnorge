package no.nav.dolly.security.oauth2.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.security.oauth2.domain.AccessScopes;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    final OnBehalfOfGenerateAccessTokenService behalfOfGenerateAccessTokenService;

    public AccessToken getAccessToken(AccessScopes accessScopes) {
        return behalfOfGenerateAccessTokenService.generateToken(accessScopes);
    }
}
