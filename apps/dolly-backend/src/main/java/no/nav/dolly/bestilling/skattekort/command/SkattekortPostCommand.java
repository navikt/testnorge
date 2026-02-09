package no.nav.dolly.bestilling.skattekort.command;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.dolly.bestilling.skattekort.domain.SokosSkattekortRequest;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.concurrent.Callable;

import static java.time.Duration.ofSeconds;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Slf4j
public class SkattekortPostCommand implements Callable<Mono<SkattekortResponse>> {

    private static final String OPPRETT_SKATTEKORT_URL = "/skattekort/api/v1/person/opprett";
    private static final String CONSUMER = "Dolly";

    private final WebClient webClient;
    private final SokosSkattekortRequest request;
    private final String token;

    @Override
    public Mono<SkattekortResponse> call() {

        log.info("Sender skattekort til Sokos: {}", Json.pretty(request));

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(OPPRETT_SKATTEKORT_URL).build())
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .map(responseEntity -> SkattekortResponse.builder()
                        .status(HttpStatus.resolve(responseEntity.getStatusCode().value()))
                        .body(null)
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(Retry.fixedDelay(3, ofSeconds(5))
                        .filter(throwable -> throwable instanceof WebClientResponseException responseException &&
                                responseException.getStatusCode().is5xxServerError())
                        .onRetryExhaustedThrow((retryBackoffSpec, lastSignal) ->
                                new RuntimeException("Retries exhausted: %s".formatted(lastSignal.failure().getMessage()))))
                .onErrorResume(throwable -> {
                    WebClientError.Description description = WebClientError.describe(throwable);
                    log.error("Feil ved sending av skattekort til Sokos. FNR: {}, Inntektsår: {}, Status: {}, Message: {}",
                            request.getFnr(),
                            request.getSkattekort() != null ? request.getSkattekort().getInntektsaar() : null,
                            description.getStatus(),
                            description.getMessage());
                    return SkattekortResponse.of(description);
                });
    }
}