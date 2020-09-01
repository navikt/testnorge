package no.nav.registre.testnorge.libs.oauth2.service;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class InsecureAuthenticationTokenResolver implements AuthenticationTokenResolver {

    @Override
    public JwtAuthenticationToken jwtAuthenticationToken() {
        throw new UnsupportedOperationException("Kan ikke hente jwt fra et usikkert miljo.");
    }

    @Override
    public boolean isClientCredentials() {
        return true;
    }

    @Override
    public void verifyAuthentication() {
        //Ignored
    }
}
