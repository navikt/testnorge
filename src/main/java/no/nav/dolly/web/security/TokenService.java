package no.nav.dolly.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.dolly.web.security.domain.AccessScopes;
import no.nav.dolly.web.security.domain.AccessToken;

@Service
@RequiredArgsConstructor
public class TokenService {
    final OnBehalfOfGenerateAccessTokenService behalfOfGenerateAccessTokenService;

    public AccessToken getAccessToken(AccessScopes accessScopes) {
        return behalfOfGenerateAccessTokenService.generateToken(accessScopes);
    }
}
