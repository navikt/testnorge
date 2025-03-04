package no.nav.dolly.bestilling.skjermingsregister.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class SkjermingsregisterPostCommand implements Callable<Flux<SkjermingDataResponse>> {

    private static final String SKJERMINGSREGISTER_URL = "/api/v1/skjerming/dolly";

    private final WebClient webClient;
    private final SkjermingDataRequest skjermingDataRequest;
    private final String token;

    @Override
    public Flux<SkjermingDataResponse> call() {

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(SKJERMINGSREGISTER_URL)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .bodyValue(skjermingDataRequest)
                .retrieve()
                .bodyToFlux(SkjermingDataResponse.class)
                .onErrorResume(error -> Flux.just(SkjermingDataResponse.builder()
                        .error(WebClientFilter.getMessage(error))
                        .build()))
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
