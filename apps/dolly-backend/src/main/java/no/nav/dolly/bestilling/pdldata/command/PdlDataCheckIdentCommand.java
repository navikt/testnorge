package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AvailibilityResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PdlDataCheckIdentCommand implements Callable<Mono<AvailibilityResponseDTO[]>> {

    private static final String PDL_FORVALTER_ORDRE_URL = "/api/v1/identer";
    private static final String IDENTER = "identer";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    public Mono<AvailibilityResponseDTO[]> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_ORDRE_URL)
                        .queryParam(IDENTER, identer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(AvailibilityResponseDTO[].class)
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}