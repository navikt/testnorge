package no.nav.dolly.bestilling.skjermingsregister.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class SkjermingsregisterPutCommand implements Callable<Mono<Void>> {

    private static final String SKJERMINGSREGISTER_URL = "/api/v1/skjermingdata";
    private static final String SKJERMINGOPPHOER_URL = SKJERMINGSREGISTER_URL + "/opphor";
    private static final String SKJERMING_TOM = "skjermetTil";

    private final WebClient webClient;
    private final String ident;
    private final LocalDateTime tilDato;
    private final String token;

    @Override
    public Mono<Void> call() {
        return webClient.put().uri(uriBuilder -> uriBuilder
                        .path(SKJERMINGOPPHOER_URL)
                        .pathSegment(ident)
                        .queryParam(SKJERMING_TOM, tilDato.toLocalDate())
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
