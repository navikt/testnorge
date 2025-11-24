package no.nav.testnav.apps.tenorsearchservice.consumers.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.dolly.v1.FinnesDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class FinnesIDollyGetCommand implements Callable<Mono<FinnesDTO>> {

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    @Override
    public Mono<FinnesDTO> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/ident/finnes")
                        .queryParam("identer", identer)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(FinnesDTO.class)
                .retryWhen(WebClientError.is5xxException());
    }

}
