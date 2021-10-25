package no.nav.testnav.libs.servletsecurity.service;
@Deprecated
public interface AuthenticationTokenResolver {
    String getTokenValue();

    boolean isClientCredentials();

    String getOid();

    void verifyAuthentication();
}
