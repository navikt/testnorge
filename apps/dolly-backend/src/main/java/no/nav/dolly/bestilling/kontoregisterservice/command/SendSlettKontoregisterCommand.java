package no.nav.dolly.bestilling.kontoregisterservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.SlettKontoRequestDTO;
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
public class SendSlettKontoregisterCommand implements Callable<Mono<String>> {
    private static final String KONTOREGISTER_API_URL = "/api/system/v1/slett-konto";

    private final WebClient webClient;
    private final SlettKontoRequestDTO body;
    private final String token;

    @Override
    public Mono<String> call() {

        log.info("Sender slett request til Bankkontoregister service:");

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(KONTOREGISTER_API_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> {
                    if (!(error instanceof WebClientResponseException)) {
                        log.error(error.getMessage(), error);
                    }
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
