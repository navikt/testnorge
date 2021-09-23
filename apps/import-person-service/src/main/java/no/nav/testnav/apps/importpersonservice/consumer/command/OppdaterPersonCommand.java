package no.nav.testnav.apps.importpersonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

import no.nav.testnav.apps.importpersonservice.consumer.request.OppdaterPersonRequest;

@RequiredArgsConstructor
public class OppdaterPersonCommand implements Callable<Mono<String>> {
    private final WebClient webClient;
    private final OppdaterPersonRequest dto;
    private final String token;

    @Override
    public Mono<String> call() {
        return webClient
                .put()
                .uri(builder -> builder
                        .path("/api/v1/personer/{ident}")
                        .build(dto.getPerson().getIdent())
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(dto), OppdaterPersonRequest.class))
                .retrieve()
                .bodyToMono(String.class);
    }
}
