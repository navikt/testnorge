package no.nav.dolly.consumer.brukerservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.brukerservice.dto.BrukerDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class BrukerServiceGetAlleCommand implements Callable<Flux<BrukerDTO>> {

    private static final String BRUKEROVERSIKT_URL = "/api/v2/brukere/alle";

    private final WebClient webClient;
    private final String token;

    @Override
    public Flux<BrukerDTO> call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BRUKEROVERSIKT_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(BrukerDTO.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }
}
