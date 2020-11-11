package no.nav.registre.testnorge.libs.oauth2.service;

import org.springframework.stereotype.Component;

@Component
public class InsecureAuthenticationTokenResolver implements AuthenticationTokenResolver {

    @Override
    public String getTokenValue() {
        throw new UnsupportedOperationException("Kan ikke hente token fra et usikkert miljo.");
    }

    @Override
    public boolean isClientCredentials() {
        return true;
    }

    @Override
    public String getOid() {
        return null;
    }

    @Override
    public void verifyAuthentication() {
        //Ignored
    }
}
