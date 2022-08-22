package no.nav.dolly.bestilling.kontoregisterservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
public class SendHentKontoregisterCommand implements Callable<HentKontoResponseDTO> {
    private static final String KONTOREGISTER_API_URL = "/kontoregister/api/kontoregister/v1/hent-konto";

    private final WebClient webClient;
    private final HentKontoRequestDTO body;
    private final String token;

    @Override
    public HentKontoResponseDTO call() {

        log.info("Sender request til Bankkontoregister service:");

        var response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(KONTOREGISTER_API_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(HentKontoResponseDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

        if (log.isTraceEnabled() && nonNull(response)) {
            log.trace("Response fra Bankkontoregister service: {}", response);
        }

        return response;
    }
}
