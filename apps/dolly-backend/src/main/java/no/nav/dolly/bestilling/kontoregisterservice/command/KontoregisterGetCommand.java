package no.nav.dolly.bestilling.kontoregisterservice.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.data.kontoregister.v1.HentKontoRequestDTO;
import no.nav.testnav.libs.data.kontoregister.v1.HentKontoResponseDTO;
import no.nav.testnav.libs.data.kontoregister.v1.KontoDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class KontoregisterGetCommand implements Callable<Mono<HentKontoResponseDTO>> {

    private static final String KONTOREGISTER_API_URL = "/api/system/v1/hent-aktiv-konto";

    private final WebClient webClient;
    private final HentKontoRequestDTO body;
    private final String token;

    @Override
    public Mono<HentKontoResponseDTO> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(KONTOREGISTER_API_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .toEntity(KontoDTO.class)
                .map(response -> HentKontoResponseDTO.builder()
                        .aktivKonto(Objects.nonNull(response.getBody()) ? response.getBody() : null)
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> Mono.just(HentKontoResponseDTO.builder()
                        .status(WebClientFilter.getStatus(throwable))
                        .build()));
    }
}
