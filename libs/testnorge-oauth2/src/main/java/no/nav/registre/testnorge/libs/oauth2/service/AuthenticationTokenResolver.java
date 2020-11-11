package no.nav.registre.testnorge.libs.oauth2.service;

public interface AuthenticationTokenResolver {
    String getTokenValue();

    boolean isClientCredentials();

    String getOid();

    void verifyAuthentication();
}
