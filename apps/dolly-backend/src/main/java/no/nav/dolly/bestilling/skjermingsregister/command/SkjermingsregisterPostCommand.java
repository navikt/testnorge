package no.nav.dolly.bestilling.skjermingsregister.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataResponse;
import no.nav.dolly.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class SkjermingsregisterPostCommand implements Callable<Mono<SkjermingsDataResponse[]>> {

    private static final String SKJERMINGSREGISTER_URL = "/api/v1/skjermingdata";

    private final WebClient webClient;
    private final List<SkjermingsDataRequest> skjermingsDataRequest;
    private final String token;

    @Override
    public Mono<SkjermingsDataResponse[]> call() {

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(SKJERMINGSREGISTER_URL)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .bodyValue(skjermingsDataRequest)
                .retrieve()
                .bodyToMono(SkjermingsDataResponse[].class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
