package no.nav.dolly.bestilling.nom.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.nom.domain.NomRessursResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class NomHentRessurs implements Callable<Mono<NomRessursResponse>> {

    private static final String HENT_RESSURS_URL = "/api/v1/dolly/hentRessurs";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<NomRessursResponse> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(HENT_RESSURS_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(ident)
                .retrieve()
                .bodyToMono(NomRessursResponse.class)
                .map(response -> {
                    response.setStatus(HttpStatus.OK);
                    return response;
                })
                .doOnError(throwable -> !(throwable instanceof WebClientResponseException.NotFound),
                        WebClientError.logTo(log))
                .onErrorResume(throwable ->
                        Mono.just(NomRessursResponse.of(WebClientError.describe(throwable))));
    }
}
