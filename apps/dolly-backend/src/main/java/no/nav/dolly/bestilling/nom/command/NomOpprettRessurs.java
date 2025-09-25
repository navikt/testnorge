package no.nav.dolly.bestilling.nom.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.nom.domain.NomRessursRequest;
import no.nav.dolly.bestilling.nom.domain.NomRessursResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class NomOpprettRessurs implements Callable<Mono<NomRessursResponse>> {

    private static final String OPPRETT_URL = "/api/v1/dolly/opprettRessurs";

    private final WebClient webClient;
    private final NomRessursRequest request;
    private final String token;

    @Override
    public Mono<NomRessursResponse> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(OPPRETT_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NomRessursResponse.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable ->
                        Mono.just(NomRessursResponse.of(WebClientError.describe(throwable))));
    }
}
