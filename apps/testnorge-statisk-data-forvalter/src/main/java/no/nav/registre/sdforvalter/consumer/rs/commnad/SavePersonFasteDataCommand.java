package no.nav.registre.sdforvalter.consumer.rs.commnad;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.personservice.v1.Gruppe;
import no.nav.testnav.libs.dto.personservice.v1.PersonDTO;

@Slf4j
@RequiredArgsConstructor
public class SavePersonFasteDataCommand implements Callable<Mono<Void>> {
    private final WebClient webClient;
    private final String token;
    private final PersonDTO dto;
    private final Gruppe gruppe;


    @Override
    public Mono<Void> call() {
        return webClient
                .put()
                .uri("/api/v1/personer")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("gruppe", gruppe.name())
                .body(BodyInserters.fromPublisher(Mono.just(dto), PersonDTO.class))
                .retrieve()
                .bodyToMono(Void.class);
    }
}
