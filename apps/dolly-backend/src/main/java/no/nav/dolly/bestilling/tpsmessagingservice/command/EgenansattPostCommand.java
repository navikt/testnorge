package no.nav.dolly.bestilling.tpsmessagingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
public class EgenansattPostCommand implements Callable<Flux<TpsMeldingResponseDTO>> {

    private static final String EGENANSATT_URL = "/api/v1/personer/{ident}/egenansatt";
    private static final String MILJOER_PARAM = "miljoer";
    private static final String EGENANSATT_FRA_PARAM = "fraOgMed";

    private final WebClient webClient;
    private final String ident;
    private final List<String> miljoer;
    private final LocalDate datoFra;
    private final String token;

    @Override
    public Flux<TpsMeldingResponseDTO> call() {

        log.info("Sender request på ident: {} til TPS messaging service, Egenansatt datoFra: {}", ident, datoFra);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(EGENANSATT_URL)
                        .queryParamIfPresent(MILJOER_PARAM, nonNull(miljoer) ? Optional.of(miljoer) : Optional.empty())
                        .queryParam(EGENANSATT_FRA_PARAM, datoFra)
                        .build(ident))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(TpsMeldingResponseDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
