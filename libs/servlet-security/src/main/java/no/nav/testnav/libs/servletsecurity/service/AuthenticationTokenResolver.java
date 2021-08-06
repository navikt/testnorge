package no.nav.testnav.libs.servletsecurity.service;

public interface AuthenticationTokenResolver {
    String getTokenValue();

    boolean isClientCredentials();

    String getOid();

    void verifyAuthentication();
}
