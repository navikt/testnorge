package no.nav.registre.testnorge.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.endringsmeldingservice.consumer.dto.TpsDoedsmeldingDTO;

@RequiredArgsConstructor
public class SendDoedsmeldingCommand implements Runnable {
    private final WebClient webClient;
    private final TpsDoedsmeldingDTO dto;
    private final String token;

    @Override
    public void run() {
        webClient
                .post()
                .uri("/api/v1/tpsmelding/doedsmelding")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(dto), TpsDoedsmeldingDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
