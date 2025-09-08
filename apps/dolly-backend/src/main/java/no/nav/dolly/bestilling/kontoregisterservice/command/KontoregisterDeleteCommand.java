package no.nav.dolly.bestilling.kontoregisterservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.kontoregister.v1.KontoregisterResponseDTO;
import no.nav.testnav.libs.data.kontoregister.v1.SlettKontoRequestDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class KontoregisterDeleteCommand implements Callable<Mono<KontoregisterResponseDTO>> {

    private static final String KONTOREGISTER_API_URL = "/api/system/v1/slett-konto";

    private final WebClient webClient;
    private final String ident;
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
                .bodyValue(new SlettKontoRequestDTO(ident, "Dolly"))
                .retrieve()
                .toBodilessEntity()
                .map(value -> KontoregisterResponseDTO.builder()
                        .status(HttpStatus.valueOf(value.getStatusCode().value()))
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> {
                    var description = WebClientError.describe(error);
                    return Mono.just(KontoregisterResponseDTO
                            .builder()
                            .status(description.getStatus())
                            .feilmelding(description.getMessage())
                            .build());
                });
    }
}