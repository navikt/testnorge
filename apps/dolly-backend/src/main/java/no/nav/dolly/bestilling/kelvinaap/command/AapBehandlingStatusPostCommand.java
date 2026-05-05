package no.nav.dolly.bestilling.kelvinaap.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.dolly.bestilling.kelvinaap.domain.AapStatusRequest;
import no.nav.dolly.bestilling.kelvinaap.domain.AapStatusResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.concurrent.Callable;

import static java.time.Duration.ofSeconds;

@RequiredArgsConstructor
@Slf4j
public class AapBehandlingStatusPostCommand implements Callable<Mono<AapStatusResponse>> {

    private static final String AAP_OPPRETT_URL = "/kelvin-aap/api/test/behandlingStatus";
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<AapStatusResponse> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(AAP_OPPRETT_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromValue(AapStatusRequest.builder()
                        .ident(ident)
                        .build()))
                .retrieve()
                .bodyToMono(AapStatusResponse.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(Retry.fixedDelay(3, ofSeconds(5))
                        .filter(throwable -> throwable instanceof WebClientResponseException responseException &&
                                             responseException.getStatusCode().is5xxServerError())
                        .onRetryExhaustedThrow(((retryBackoffSpec, lastSignal) ->
                                new RuntimeException("Retries exhausted: %s".formatted(lastSignal.failure().getMessage())))))
                .onErrorResume(error -> {
                    val feilmelding = WebClientError.describe(error);
                    return Mono.just(AapStatusResponse.builder()
                            .status(feilmelding.getStatus())
                            .error(feilmelding.getMessage())
                            .build());
                });
    }
}