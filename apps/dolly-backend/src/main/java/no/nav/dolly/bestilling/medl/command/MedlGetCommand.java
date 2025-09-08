package no.nav.dolly.bestilling.medl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.medl.MedlDataResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class MedlGetCommand implements Callable<Flux<MedlDataResponse>> {

    private static final String MEDL_URL = "/rest/v1/person/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Flux<MedlDataResponse> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(MEDL_URL)
                        .build(ident))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(MedlDataResponse.class)
                .doOnError(
                        throwable -> !(throwable instanceof WebClientResponseException.NotFound),
                        WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(WebClientResponseException.NotFound.class::isInstance, throwable -> Flux.empty());
    }

}
