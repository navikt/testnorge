package no.nav.testnav.libs.reactivesecurity.exchange;

import reactor.core.publisher.Mono;

import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@FunctionalInterface
public interface GenerateToken {
    Mono<AccessToken> generateToken(ServerProperties serverProperties);
}
