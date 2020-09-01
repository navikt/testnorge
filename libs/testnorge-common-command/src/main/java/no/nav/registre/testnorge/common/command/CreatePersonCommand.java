package no.nav.registre.testnorge.common.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;

@Slf4j
@DependencyOn("person-api")
@RequiredArgsConstructor
public class CreatePersonCommand implements Runnable {
    private final WebClient webClient;
    private final PersonDTO person;
    private final String accessToken;

    @Override
    public void run() {
        log.info("Oppretter {}...", person.getIdent());
        webClient
                .post()
                .uri(builder -> builder.path("/v1/personer").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(BodyInserters.fromPublisher(Mono.just(person), PersonDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        log.info("Person {} opprettet.", person.getIdent());
    }
}