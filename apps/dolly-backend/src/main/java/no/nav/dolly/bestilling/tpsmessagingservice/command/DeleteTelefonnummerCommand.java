package no.nav.dolly.bestilling.tpsmessagingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.http.HttpHeaders;
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
public class DeleteTelefonnummerCommand implements Callable<List<TpsMeldingResponseDTO>> {

    private static final String MILJOER_PARAM = "miljoer";
    private static final String TELEFONTYPER_PARAM = "telefontyper";

    private final WebClient webClient;
    private final String ident;
    private final List<String> miljoer;
    private final List<String> telefontyper;
    private final String urlPath;
    private final String token;

    @Override
    public List<TpsMeldingResponseDTO> call() {

        log.trace("Sender delete request på ident: {} til TPS messaging service", ident);

        var response = webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(urlPath)
                        .queryParam(TELEFONTYPER_PARAM, telefontyper)
                        .queryParamIfPresent(MILJOER_PARAM, nonNull(miljoer) ? Optional.of(miljoer) : Optional.empty())
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(TpsMeldingResponseDTO[].class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

        if (log.isTraceEnabled() && nonNull(response)) {
            Stream.of(response).forEach(entry -> log.trace("Response fra tps-messaging-service delete telefonnummer: {}", entry));
        }
        return Arrays.asList(response);
    }
}
