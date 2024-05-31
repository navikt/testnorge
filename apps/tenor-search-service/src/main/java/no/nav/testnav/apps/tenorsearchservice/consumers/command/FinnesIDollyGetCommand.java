package no.nav.testnav.apps.tenorsearchservice.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.dolly.v1.FinnesDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class FinnesIDollyGetCommand implements Callable<Mono<FinnesDTO>> {

    private static final String FINNES_URL = "/api/v1/ident/finnes";
    private static final String IDENTER = "identer";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    public Mono<FinnesDTO> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(FINNES_URL)
                        .queryParam(IDENTER, identer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(FinnesDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
