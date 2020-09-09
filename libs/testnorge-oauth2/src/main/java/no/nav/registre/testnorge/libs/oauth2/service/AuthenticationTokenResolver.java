package no.nav.registre.testnorge.libs.oauth2.service;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface AuthenticationTokenResolver {
    JwtAuthenticationToken jwtAuthenticationToken();
    boolean isClientCredentials();
    void verifyAuthentication();
}
