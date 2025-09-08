package no.nav.dolly.bestilling.skjermingsregister.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class SkjermingsregisterPutCommand implements Callable<Mono<SkjermingDataResponse>> {

    private static final String SKJERMINGSREGISTER_URL = "/api/v1/skjerming/dolly";

    private final WebClient webClient;
    private final SkjermingDataRequest skjermingsDataRequest;
    private final String token;

    @Override
    public Mono<SkjermingDataResponse> call() {
        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(SKJERMINGSREGISTER_URL).build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(skjermingsDataRequest)
                .retrieve()
                .toBodilessEntity()
                .map(result -> SkjermingDataResponse.builder().build())
                .onErrorResume(throwable -> SkjermingDataResponse.of(WebClientError.describe(throwable)))
                .doOnError(WebClientError.logTo(log));
    }
}
