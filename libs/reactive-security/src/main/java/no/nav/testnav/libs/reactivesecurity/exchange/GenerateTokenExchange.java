package no.nav.testnav.libs.reactivesecurity.exchange;

import reactor.core.publisher.Mono;

import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;

@FunctionalInterface
public interface GenerateTokenExchange {
    Mono<AccessToken> generateToken(ServerProperties serverProperties);
}
