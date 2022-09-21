package no.nav.dolly.bestilling.kontoregisterservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class SendOppdaterKontoregisterCommand implements Callable<Mono<String>> {
    private static final String KONTOREGISTER_API_URL = "/api/system/v1/oppdater-konto";

    private final WebClient webClient;
    private final OppdaterKontoRequestDTO body;
    private final String token;

    private String getErrorMessage(Throwable error) {

        return error instanceof WebClientResponseException webClientResponseException ?
                webClientResponseException.getResponseBodyAsString() : error.getMessage();
    }

    @Override
    public Mono<String> call() {
        log.info("Sender request til Bankkontoregister service: {}", body.getKontonummer());

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(KONTOREGISTER_API_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .flatMap(value -> Mono.just("OK"))
                .doOnError(error -> {
                    if (!(error instanceof WebClientResponseException)) {
                        log.error(error.getMessage(), error);
                    }
                })
                .onErrorResume(e -> Mono.just("Feil= " + getErrorMessage(e)))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
