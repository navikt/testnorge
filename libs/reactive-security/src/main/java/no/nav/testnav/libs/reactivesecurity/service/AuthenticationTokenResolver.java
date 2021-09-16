package no.nav.testnav.libs.reactivesecurity.service;

import reactor.core.publisher.Mono;

import no.nav.testnav.libs.reactivesecurity.domain.Token;

public interface  AuthenticationTokenResolver {
    Mono<Token> getToken();
    Mono<String> getClientRegistrationId();
}
