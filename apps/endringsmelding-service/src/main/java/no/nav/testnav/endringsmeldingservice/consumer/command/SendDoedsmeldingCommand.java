package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.endringsmeldingservice.consumer.dto.DoedsmeldingDTO;
import no.nav.testnav.endringsmeldingservice.consumer.response.EndringsmeldingResponse;

@Slf4j
@RequiredArgsConstructor
public class SendDoedsmeldingCommand implements Callable<Mono<EndringsmeldingResponse>> {
    private final WebClient webClient;
    private final DoedsmeldingDTO dto;
    private final String token;

    @Override
    public Mono<EndringsmeldingResponse> call() {
        return webClient
                .post()
                .uri("/api/v1/tpsmelding/doedsmelding")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(dto), DoedsmeldingDTO.class))
                .retrieve()
                .bodyToMono(EndringsmeldingResponse.class)
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        log.error(
                                "Feil ved innsendelse av dødseslmelding. Feilmelding: {}.",
                                ((WebClientResponseException) error).getResponseBodyAsString()
                        );
                    } else {
                        log.error("Feil ved innsendelse av dødseslmelding.", error);
                    }
                });
    }
}
