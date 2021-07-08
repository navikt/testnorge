package no.nav.testnav.libs.security.service;

import reactor.core.publisher.Mono;

import no.nav.testnav.libs.security.domain.Token;

public interface  AuthenticationTokenResolver {
    Mono<Token> getToken();
}
