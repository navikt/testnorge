package no.nav.registre.testnorge.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.endringsmeldingservice.consumer.request.TpsFoedselsmeldingRequest;
import no.nav.registre.testnorge.endringsmeldingservice.consumer.response.TpsFoedselsmeldingResponse;

@Slf4j
@RequiredArgsConstructor
public class SendFoedselsmeldingCommand implements Callable<TpsFoedselsmeldingResponse> {
    private final WebClient webClient;
    private final TpsFoedselsmeldingRequest request;
    private final String token;

    @Override
    public TpsFoedselsmeldingResponse call() {
        try {
            return webClient
                    .post()
                    .uri("/api/v1/tpsmelding/foedselsmelding")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(BodyInserters.fromPublisher(Mono.just(request), TpsFoedselsmeldingRequest.class))
                    .retrieve()
                    .bodyToMono(TpsFoedselsmeldingResponse.class)
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
