package no.nav.dolly.consumer.altinn3.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.altinn3.dto.Altinn3TilgangDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class Altinn3TilgangServiceGetCommand implements Callable<Flux<Altinn3TilgangDTO>> {

    private static final String ORGANISASJONER_URL = "/api/v1/organisasjoner";

    private final WebClient webClient;
    private final String token;

    @Override
    public Flux<Altinn3TilgangDTO> call() {

        return webClient
                .get()
                .uri(path -> path.path(ORGANISASJONER_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(Altinn3TilgangDTO.class);
    }
}
