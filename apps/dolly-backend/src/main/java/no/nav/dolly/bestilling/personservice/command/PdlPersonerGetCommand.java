package no.nav.dolly.bestilling.personservice.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class PdlPersonerGetCommand implements Callable<Flux<PdlPersonBolk>> {

    private static final String PERSON_SERVICE_URL = "/api/v2/personer/identer";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    @Override
    public Flux<PdlPersonBolk> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PERSON_SERVICE_URL)
                        .queryParam("identer", identer)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(PdlPersonBolk.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}
