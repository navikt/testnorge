package no.nav.registre.testnorge.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.endringsmeldingservice.consumer.dto.TpsDoedsmeldingDTO;

@Slf4j
@RequiredArgsConstructor
public class SendDoedsmeldingCommand implements Runnable {
    private final WebClient webClient;
    private final TpsDoedsmeldingDTO dto;
    private final String token;

    @Override
    public void run() {
        try {
            webClient
                    .post()
                    .uri("/api/v1/tpsmelding/doedsmelding")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(BodyInserters.fromPublisher(Mono.just(dto), TpsDoedsmeldingDTO.class))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error(
                    "Feil ved innsendelse av foedseslmelding. Feilmelding: {}.",
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }
}
