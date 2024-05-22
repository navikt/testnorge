package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class PersonExistsGetCommand implements Callable<Flux<Boolean>> {

    private static final String PERSON_URL = "/api/v1/personer/{ident}/exists";

    private final WebClient webClient;
    private final String ident;
    private final String token;


    @Override
    public Flux<Boolean> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PERSON_URL)
                        .build(ident))
                .header(AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(Boolean.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
