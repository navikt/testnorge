package no.nav.testnav.apps.organisasjontilgangfrontend.router;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class LoginHandler {

    public Mono<ServerResponse> forward(ServerRequest request) {
        request.queryParam("error").ifPresent(error -> {
            if (Boolean.parseBoolean(error)) {
                log.warn("Bruker har ikke tilgang til appen.");
            }
        });
        return ServerResponse.ok().body(BodyInserters.fromValue("forward:/"));
    }
}
