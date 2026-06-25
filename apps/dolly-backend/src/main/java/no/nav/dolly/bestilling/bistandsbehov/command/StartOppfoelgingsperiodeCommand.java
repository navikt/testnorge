package no.nav.dolly.bestilling.bistandsbehov.command;

import lombok.RequiredArgsConstructor;
import lombok.val;
import no.nav.dolly.bestilling.bistandsbehov.dto.OppfoelgingRequestDTO;
import no.nav.dolly.bestilling.bistandsbehov.dto.ResponseStatusDTO;
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
public class StartOppfoelgingsperiodeCommand implements Callable<Mono<ResponseStatusDTO>> {

    private static final String OPPFOELGING_URL = "/oppfoelging/veilarboppfolging/api/v1/dolly/startOppfolgingsperiode";
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<ResponseStatusDTO> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(OPPFOELGING_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(OppfoelgingRequestDTO.builder()
                        .fnr(ident)
                        .build())
                .retrieve()
                .toBodilessEntity()
                .map(status -> ResponseStatusDTO.builder()
                        .status(HttpStatus.valueOf(status.getStatusCode().value()))
                        .build())
                .retryWhen(Retry.fixedDelay(3, ofSeconds(5))
                        .filter(throwable -> throwable instanceof WebClientResponseException responseException &&
                                             responseException.getStatusCode().is5xxServerError())
                        .onRetryExhaustedThrow(((_, lastSignal) ->
                                new RuntimeException("Retries exhausted: %s".formatted(lastSignal.failure().getMessage())))))
                .onErrorResume(error -> {
                    val feilmelding = WebClientError.describe(error);
                    return Mono.just(ResponseStatusDTO.builder()
                            .status(feilmelding.getStatus())
                            .reason(feilmelding.getMessage())
                            .build());
                });
    }
}
