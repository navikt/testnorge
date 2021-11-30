package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

interface ExchangeCommand extends Callable<Mono<AccessToken>> {

    default String toScope(ServerProperties serverProperties) {
        return "api://" + serverProperties.getCluster() + "." + serverProperties.getNamespace() + "." + serverProperties.getName() + "/.default";
    }

}
