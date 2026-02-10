package no.nav.dolly.bestilling.skattekort.command;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortDTO;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortHentRequest;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static java.time.Duration.ofSeconds;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
public class SkattekortHentCommand implements Callable<Mono<SkattekortResponse>> {

    private static final String OPPRETT_SKATTEKORT_URL = "/skattekort/api/v1/person/hent-skattekort";

    private final WebClient webClient;
    private final SkattekortHentRequest request;
    private final String token;

    @Override
    public Mono<SkattekortResponse> call() {

        log.info("Henter skattekort fra Sokos: {}", Json.pretty(request));

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(OPPRETT_SKATTEKORT_URL).build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SkattekortDTO[].class)
                .map(skattekort -> SkattekortResponse.builder()
                        .status(HttpStatus.OK)
                        .skattekort(Arrays.asList(skattekort))
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(Retry.fixedDelay(3, ofSeconds(5))
                        .filter(throwable -> throwable instanceof WebClientResponseException responseException &&
                                responseException.getStatusCode().is5xxServerError())
                        .onRetryExhaustedThrow((retryBackoffSpec, lastSignal) ->
                                new RuntimeException("Retries exhausted: %s".formatted(lastSignal.failure().getMessage()))))
                .onErrorResume(throwable -> {
                    WebClientError.Description description = WebClientError.describe(throwable);
                    log.error("Feil ved Henting av skattekort til Sokos. FNR: {}, Inntektsår: {}, Status: {}, Message: {}",
                            request.getFnr(),
                            nonNull(request.getInntektsaar()) ? request.getInntektsaar() : null,
                            description.getStatus(),
                            description.getMessage());
                    return SkattekortResponse.of(description);
                });
    }
}