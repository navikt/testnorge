package no.nav.testnav.apps.brukerservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.brukerservice.consumer.dto.CurrentBrukerDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetCurrentBrukerCommand implements Callable<Mono<CurrentBrukerDTO>> {

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<CurrentBrukerDTO> call() {

        return webClient
                .get()
                .uri("/api/v1/bruker/current")
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(CurrentBrukerDTO.class);
    }
}
