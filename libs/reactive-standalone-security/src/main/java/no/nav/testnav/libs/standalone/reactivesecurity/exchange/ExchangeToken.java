package no.nav.testnav.libs.standalone.reactivesecurity.exchange;

import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import reactor.core.publisher.Mono;


@FunctionalInterface
public interface ExchangeToken {
    Mono<AccessToken> exchange(ServerProperties serverProperties);
}
