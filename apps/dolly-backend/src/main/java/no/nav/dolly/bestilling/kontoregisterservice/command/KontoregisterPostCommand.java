package no.nav.dolly.bestilling.kontoregisterservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.kontoregister.v1.KontoregisterResponseDTO;
import no.nav.testnav.libs.data.kontoregister.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class KontoregisterPostCommand implements Callable<Mono<KontoregisterResponseDTO>> {

    private static final String KONTOREGISTER_API_URL = "/api/system/v1/oppdater-konto";

    private final WebClient webClient;
    private final OppdaterKontoRequestDTO body;
    private final String token;

    @Override
    public Mono<KontoregisterResponseDTO> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(KONTOREGISTER_API_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .map(value -> KontoregisterResponseDTO.builder()
                        .status(HttpStatus.valueOf(value.getStatusCode().value()))
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    return Mono.just(KontoregisterResponseDTO
                            .builder()
                            .status(description.getStatus())
                            .feilmelding(description.getMessage())
                            .build());
                })
                .retryWhen(WebClientError.is5xxException());
    }

}