package no.nav.dolly.consumer.brukerservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.brukerservice.dto.BrukereDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class BrukerServiceGetOrgBrukereCommand implements Callable<Mono<BrukereDTO>> {

    private static final String TILGANG_URL = "/api/v1/tilgang/orgnr/{orgnr}";

    private final WebClient webClient;
    private final String orgnr;
    private final String token;

    @Override
    public Mono<BrukereDTO> call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TILGANG_URL)
                        .build(orgnr))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(BrukereDTO.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(_ -> Mono.just(BrukereDTO.builder()
                        .brukere(List.of("__OPPSLAG_FEILET__"))
                        .build()));
    }
}
