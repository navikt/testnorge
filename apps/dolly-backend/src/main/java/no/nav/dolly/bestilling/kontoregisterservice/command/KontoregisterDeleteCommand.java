package no.nav.dolly.bestilling.kontoregisterservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.KontoregisterResponseDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.SlettKontoRequestDTO;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Slf4j
public class KontoregisterDeleteCommand implements Callable<Mono<KontoregisterResponseDTO>> {

    private static final String KONTOREGISTER_API_URL = "/api/system/v1/slett-konto";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<KontoregisterResponseDTO> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(KONTOREGISTER_API_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + token)
                .bodyValue(new SlettKontoRequestDTO(ident, "Dolly"))
                .retrieve()
                .toBodilessEntity()
                .map(value -> KontoregisterResponseDTO.builder()
                        .status(value.getStatusCode())
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(KontoregisterResponseDTO.builder()
                        .status(WebClientFilter.getStatus(error))
                        .feilmelding(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
