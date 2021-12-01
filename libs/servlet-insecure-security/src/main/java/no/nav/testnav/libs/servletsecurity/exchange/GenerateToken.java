package no.nav.testnav.libs.servletsecurity.exchange;

import reactor.core.publisher.Mono;

import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@FunctionalInterface
public interface GenerateToken {
    Mono<AccessToken> generateToken(ServerProperties serverProperties);
}
