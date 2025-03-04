package no.nav.dolly.bestilling.skjermingsregister.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class SkjermingsregisterGetCommand implements Callable<Mono<SkjermingDataResponse>> {

    private static final String SKJERMINGSREGISTER_URL = "/api/v1/skjerming/dolly";
    private static final String PERSONIDENT_HEADER = "personident";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<SkjermingDataResponse> call() {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(SKJERMINGSREGISTER_URL)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(PERSONIDENT_HEADER, ident)
                .retrieve()
                .bodyToMono(SkjermingDataResponse.class)
                .onErrorResume(WebClientResponseException.NotFound.class::isInstance,
                        error -> Mono.just(SkjermingDataResponse.builder()
                                .eksistererIkke(true)
                                .build()))
                .onErrorResume(error -> Mono.just(SkjermingDataResponse.builder()
                        .error(WebClientFilter.getMessage(error))
                        .build()))
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
