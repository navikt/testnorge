package no.nav.dolly.bestilling.tpsmessagingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
public class TelefonnummerDeleteCommand implements Callable<Flux<TpsMeldingResponseDTO>> {

    private static final String TELEFONNUMMER_URL = "/api/v1/personer/{ident}/telefonnumre";
    private static final String MILJOER_PARAM = "miljoer";
    private static final String TELEFONTYPER_PARAM = "telefontyper";

    private final WebClient webClient;
    private final String ident;
    private final List<String> miljoer;
    private final List<String> telefontyper;
    private final String token;

    @Override
    public Flux<TpsMeldingResponseDTO> call() {

        log.trace("Sender delete request på ident: {} til TPS messaging service", ident);

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TELEFONNUMMER_URL)
                        .queryParam(TELEFONTYPER_PARAM, telefontyper)
                        .queryParamIfPresent(MILJOER_PARAM, nonNull(miljoer) ? Optional.of(miljoer) : Optional.empty())
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(TpsMeldingResponseDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
