package no.nav.testnav.libs.core.router;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class InternalHandler {
    public Mono<ServerResponse> isAlive(ServerRequest request) {
        return ServerResponse.ok().body(BodyInserters.fromValue("OK"));
    }
    public Mono<ServerResponse> isReady(ServerRequest request) {
        return ServerResponse.ok().body(BodyInserters.fromValue("OK"));
    }
}
