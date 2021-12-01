package no.nav.testnav.libs.securitycore.command;

import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

public interface ExchangeCommand extends Callable<Mono<AccessToken>> {
}
