package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.endringsmeldingservice.consumer.request.FoedselsmeldingRequest;
import no.nav.testnav.endringsmeldingservice.consumer.response.EndringsmeldingResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class SendFoedselsmeldingCommand implements Callable<Mono<EndringsmeldingResponse>> {
    private final WebClient webClient;
    private final FoedselsmeldingRequest request;
    private final String token;

    @Override
    public Mono<EndringsmeldingResponse> call() {
        return webClient
                .post()
                .uri("/api/v1/tpsmelding/foedselsmelding")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(request), FoedselsmeldingRequest.class))
                .retrieve()
                .bodyToMono(EndringsmeldingResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        log.error(
                                "Feil ved innsendelse av fødseslmelding. Feilmelding: {}.",
                                ((WebClientResponseException) error).getResponseBodyAsString()
                        );
                    } else {
                        log.error("Feil ved innsendelse av fødseslmelding.", error);
                    }
                });
    }
}
