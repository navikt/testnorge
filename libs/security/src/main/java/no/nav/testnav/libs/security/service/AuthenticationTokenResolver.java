package no.nav.testnav.libs.security.service;

import reactor.core.publisher.Mono;

public interface AuthenticationTokenResolver {
    Mono<String> getTokenValue();

    boolean isClientCredentials();

    String getOid();

    void verifyAuthentication();
}
