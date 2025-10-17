package no.nav.dolly.bestilling.skjermingsregister.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class SkjermingsregisterGetCommand implements Callable<Mono<SkjermingDataResponse>> {

    private static final String SKJERMINGSREGISTER_URL = "/api/v1/skjerming/dolly";
    private static final String PERSONIDENT_HEADER = "personident";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<SkjermingDataResponse> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(SKJERMINGSREGISTER_URL).build())
                .headers(WebClientHeader.bearer(token))
                .header(PERSONIDENT_HEADER, ident)
                .retrieve()
                .bodyToMono(SkjermingDataResponse.class)
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(WebClientResponseException.NotFound.class::isInstance,
                        error -> Mono.just(SkjermingDataResponse.builder()
                                .eksistererIkke(true)
                                .build()))
                .onErrorResume(throwable -> SkjermingDataResponse.of(WebClientError.describe(throwable)))
                .doOnError(WebClientError.logTo(log));
    }
}
