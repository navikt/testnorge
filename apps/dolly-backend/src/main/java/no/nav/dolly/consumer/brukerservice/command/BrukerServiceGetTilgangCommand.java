package no.nav.dolly.consumer.brukerservice.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.brukerservice.dto.TilgangDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class BrukerServiceGetTilgangCommand implements Callable<Mono<TilgangDTO>> {

    private static final String TILGANG_URL = "/api/v1/tilgang";

    private final WebClient webClient;
    private final String brukerId;
    private final String token;

    @Override
    public Mono<TilgangDTO> call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TILGANG_URL)
                        .queryParam("brukerId", brukerId)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(TilgangDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(TilgangDTO.builder()
                        .brukere(List.of(brukerId))
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
