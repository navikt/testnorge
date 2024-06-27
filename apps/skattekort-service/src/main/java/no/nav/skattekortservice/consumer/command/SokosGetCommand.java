package no.nav.skattekortservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.skattekortservice.dto.SokosResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class SokosGetCommand implements Callable<Flux<SokosResponse>> {

    private static final String SOKOS_URL = "/api/v1/skattekort/hent/{fnr}";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<SokosResponse> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(SOKOS_URL)
                        .build(ident))
                .header(AUTHORIZATION, "Bearer " + token)
                .header("korrelasjonsid", UUID.randomUUID().toString())
                .retrieve()
                .bodyToFlux(SokosResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}