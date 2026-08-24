package no.nav.dolly.consumer.brukerservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.brukerservice.dto.BrukerDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class BrukerServiceGetTilgangCommand implements Callable<Mono<BrukerDTO>> {

    private static final String TILGANG_URL = "/api/v2/brukere/{id}";

    private final WebClient webClient;
    private final String brukerId;
    private final String token;

    @Override
    public Mono<BrukerDTO> call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TILGANG_URL)
                        .queryParam("id", brukerId)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(BrukerDTO.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(_ -> Mono.just(BrukerDTO.builder()
                        .id(brukerId)
                        .build()));
    }
}
