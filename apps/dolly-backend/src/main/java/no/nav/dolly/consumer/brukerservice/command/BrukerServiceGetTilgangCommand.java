package no.nav.dolly.consumer.brukerservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.brukerservice.dto.TilgangDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
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
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(TilgangDTO.class)
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> Mono.just(TilgangDTO.builder()
                        .brukere(List.of(brukerId))
                        .build()));
    }
}
