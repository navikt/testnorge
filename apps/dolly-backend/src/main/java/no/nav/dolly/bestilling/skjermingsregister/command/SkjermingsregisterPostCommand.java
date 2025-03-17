package no.nav.dolly.bestilling.skjermingsregister.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Slf4j
public class SkjermingsregisterPostCommand implements Callable<Flux<SkjermingDataResponse>> {

    private static final String SKJERMINGSREGISTER_URL = "/api/v1/skjerming/dolly";

    private final WebClient webClient;
    private final SkjermingDataRequest skjermingDataRequest;
    private final String token;

    @Override
    public Flux<SkjermingDataResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(SKJERMINGSREGISTER_URL).build())
                .header(AUTHORIZATION, "Bearer " + token)
                .bodyValue(skjermingDataRequest)
                .retrieve()
                .bodyToFlux(SkjermingDataResponse.class)
                .onErrorResume(throwable -> SkjermingDataResponse.of(WebClientError.describe(throwable)))
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException());
    }

}
