package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

abstract class ExchangeCommand implements Callable<Mono<AccessToken>> {

    String toScope(ServerProperties serverProperties) {
        return "api://" + serverProperties.getCluster() + "." + serverProperties.getNamespace() + "." + serverProperties.getName() + "/.default";
    }

}
