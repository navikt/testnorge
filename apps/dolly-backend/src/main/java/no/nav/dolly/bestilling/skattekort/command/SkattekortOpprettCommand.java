package no.nav.dolly.bestilling.skattekort.command;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortRequest;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.concurrent.Callable;

import static java.time.Duration.ofSeconds;

@RequiredArgsConstructor
@Slf4j
public class SkattekortOpprettCommand implements Callable<Mono<SkattekortResponse>> {

    private static final String OPPRETT_SKATTEKORT_URL = "/skattekort/api/v1/person/opprett";

    private final WebClient webClient;
    private final SkattekortRequest request;
    private final String token;

    @Override
    public Mono<SkattekortResponse> call() {

        log.info("Sender skattekort til Sokos: {}", Json.pretty(request));

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(OPPRETT_SKATTEKORT_URL).build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .map(responseEntity -> SkattekortResponse.builder()
                        .status(HttpStatus.valueOf(responseEntity.getStatusCode().value()))
                        .skattekort(null)
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(Retry.fixedDelay(3, ofSeconds(5))
                        .filter(throwable -> throwable instanceof WebClientResponseException responseException &&
                                responseException.getStatusCode().is5xxServerError())
                        .onRetryExhaustedThrow(((retryBackoffSpec, lastSignal) ->
                                new RuntimeException("Retries exhausted: %s".formatted(lastSignal.failure().getMessage())))))
                .onErrorResume(throwable ->
                    SkattekortResponse.of(WebClientError.describe(throwable)));
    }
}