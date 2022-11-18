package no.nav.dolly.bestilling.kontoregisterservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class KontoregisterGetCommand implements Callable<Mono<HentKontoResponseDTO>> {

    private static final String KONTOREGISTER_API_URL = "/api/system/v1/hent-konto";

    private final WebClient webClient;
    private final HentKontoRequestDTO body;
    private final String token;

    @Override
    public Mono<HentKontoResponseDTO> call() {

        log.info("Sender request til Bankkontoregister service:");

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(KONTOREGISTER_API_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(HentKontoResponseDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
