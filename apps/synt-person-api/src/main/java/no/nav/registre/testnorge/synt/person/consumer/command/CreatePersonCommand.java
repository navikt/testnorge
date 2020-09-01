package no.nav.registre.testnorge.synt.person.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.synt.person.domain.Person;

@Slf4j
@RequiredArgsConstructor
public class CreatePersonCommand implements Runnable {
    private final WebClient webClient;
    private final Person person;

    @Override
    public void run() {
        var person = this.person.toDTO();
        log.info("Oppretter {}", person.getIdent());
        webClient
                .post()
                .uri(builder -> builder.path("/v1/personer").build())
                .body(BodyInserters.fromPublisher(Mono.just(person), PersonDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
