package no.nav.dolly.consumer.brukerservice.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class BrukerServiceGetTilgangCommand implements Callable<Mono<List<String>>> {

    private static final String TILGANG_URL = "/api/v1/tilgang";

    private final WebClient webClient;
    private final String brukerId;
    private final String token;

    @Override
    public Mono<List<String>> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(TILGANG_URL)
                        .queryParam("brukerId", brukerId)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String[].class)
                .map(List::of)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(List.of(brukerId)))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
