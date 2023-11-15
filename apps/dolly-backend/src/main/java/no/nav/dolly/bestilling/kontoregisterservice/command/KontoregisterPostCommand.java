package no.nav.dolly.bestilling.kontoregisterservice.command;

import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.KontoregisterResponseDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class KontoregisterPostCommand implements Callable<Mono<KontoregisterResponseDTO>> {

    private static final String KONTOREGISTER_API_URL = "/api/system/v1/oppdater-konto";

    private final WebClient webClient;
    private final OppdaterKontoRequestDTO body;
    private final String token;

    @Override
    public Mono<KontoregisterResponseDTO> call() {

        long startTid = System.currentTimeMillis();

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(KONTOREGISTER_API_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .map(value -> KontoregisterResponseDTO.builder()
                        .status(HttpStatus.valueOf(value.getStatusCode().value()))
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> error instanceof ConnectTimeoutException ||
                        error instanceof ReadTimeoutException ||
                        error instanceof WriteTimeoutException ?
                        Mono.just(KontoregisterResponseDTO.builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .feilmelding(String.format("Oppretting av bankkonto gitt opp etter %d sekunder",
                                        (System.currentTimeMillis() - startTid)/1000))
                                .build()) :
                    Mono.just(KontoregisterResponseDTO.builder()
                        .status(WebClientFilter.getStatus(error))
                        .feilmelding(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
