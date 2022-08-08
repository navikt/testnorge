package no.nav.dolly.bestilling.kontoregisterservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.kontoregisterservice.dto.OppdaterKontoRequestDto;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
public class SendOppdaterKontoregisterCommand implements Callable<List<TpsMeldingResponseDTO>> {
    private static final String BASE_URL = "/api/v1/personer/{ident}";
    private static final String BANKKONTO_URL = BASE_URL + "/bankkonto-utenlandsk";

    private final WebClient webClient;
    private final OppdaterKontoRequestDto body;
    private final String token;

    @Override
    public List<TpsMeldingResponseDTO> call() {

        log.info("Sender request på ident: {} til Bankkontoregister service: {}", body.getKontohaver(), body);

        var response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(BANKKONTO_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TpsMeldingResponseDTO[].class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

        if (log.isTraceEnabled() && nonNull(response)) {
            Stream.of(response).forEach(entry -> log.trace("Response fra TPS messaging service: {}", entry));
        }

        return Arrays.asList(response);
    }
}
