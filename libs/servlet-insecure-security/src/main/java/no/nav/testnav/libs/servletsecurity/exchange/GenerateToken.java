package no.nav.testnav.libs.servletsecurity.exchange;

import reactor.core.publisher.Mono;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;


@FunctionalInterface
public interface GenerateToken {
    Mono<AccessToken> generateToken(ServerProperties serverProperties);
}
