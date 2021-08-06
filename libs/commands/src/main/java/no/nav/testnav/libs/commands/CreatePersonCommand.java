package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.dto.person.v1.PersonDTO;

@Slf4j
@RequiredArgsConstructor
public class CreatePersonCommand implements Runnable {
    private final WebClient webClient;
    private final PersonDTO person;
    private final String accessToken;
    private final String kilde;

    @Override
    public void run() {
        log.info("Oppretter {}...", person.getIdent());
        webClient
                .post()
                .uri(builder -> builder.path("/api/v1/personer").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("kilde", kilde)
                .body(BodyInserters.fromPublisher(Mono.just(person), PersonDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        log.info("Person {} opprettet.", person.getIdent());
    }
}