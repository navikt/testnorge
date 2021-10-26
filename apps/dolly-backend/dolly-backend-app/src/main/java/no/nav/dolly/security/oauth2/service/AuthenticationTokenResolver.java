package no.nav.dolly.security.oauth2.service;

public interface AuthenticationTokenResolver {
    String getTokenValue();

    boolean isClientCredentials();

    String getOid();

    void verifyAuthentication();
}
