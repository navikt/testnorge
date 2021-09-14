package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.reactivesessionsecurity.domain.Token;

@FunctionalInterface
public interface TokenResolver {

    Mono<Token> getToken(ServerWebExchange exchange);

}
