package no.nav.registre.testnorge.libs.oauth2.service;

import org.springframework.security.oauth2.core.AbstractOAuth2Token;

public interface AuthenticationTokenResolver {
    String getTokenValue();

    boolean isClientCredentials();

    String getOid();

    void verifyAuthentication();
}
