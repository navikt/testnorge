package no.nav.testnav.apps.oversiktfrontend.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.BrukerDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetBrukerCommand implements Callable<Mono<BrukerDTO>> {

    private final WebClient webClient;
    private final String token;
    private final String orgnummer;

    @Override
    public Mono<BrukerDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/brukere")
                        .queryParam("organisasjonsnummer", orgnummer).
                        build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(BrukerDTO.class)
                .retryWhen(WebClientError.is5xxException())
                .next();
    }

}
