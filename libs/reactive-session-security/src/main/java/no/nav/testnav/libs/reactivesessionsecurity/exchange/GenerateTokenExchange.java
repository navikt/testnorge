package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@FunctionalInterface
public interface GenerateTokenExchange {
    Mono<AccessToken> generateToken(ServerProperties serverProperties, ServerWebExchange exchange);
}
