package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.util.List;
import java.util.concurrent.Callable;

import static java.time.Duration.ofSeconds;

@Slf4j
@RequiredArgsConstructor
public class InntektstubPostCommand implements Callable<Flux<Inntektsinformasjon>> {

    private static final String INNTEKTER_URL = "/inntektstub/api/v2/inntektsinformasjon";

    private final WebClient webClient;
    private final List<Inntektsinformasjon> inntektsinformasjon;
    private final String token;

    @Override
    public Flux<Inntektsinformasjon> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(INNTEKTER_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(inntektsinformasjon)
                .retrieve()
                .bodyToFlux(Inntektsinformasjon.class)
                .retryWhen(Retry.fixedDelay(3, ofSeconds(5))
                        .filter(throwable -> throwable instanceof WebClientResponseException responseException &&
                                responseException.getStatusCode().is5xxServerError())
                        .onRetryExhaustedThrow(((retryBackoffSpec, lastSignal) ->
                                new RuntimeException("Retries exhausted: %s".formatted(lastSignal.failure().getMessage())))))
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    log.error("Lagring av Instdata feilet: {}", description.getMessage(), throwable);
                    return Inntektsinformasjon.of(description);
                });
    }
}