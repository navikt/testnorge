package no.nav.identpool.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.dto.TpsIdentStatusDTO;
import no.nav.identpool.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class TpsMessagingGetCommand implements Callable<Flux<TpsIdentStatusDTO>> {

    private static final String TPS_MESSAGING_URL = "/api/v1/identer";
    private static final String IDENTER = "identer";
    private static final String MILJOER = "miljoer";
    private static final String INCLUDE_PROD = "includeProd";

    private final WebClient webClient;
    private final String token;
    private final List<String> idents;
    private final Set<String> envs;
    private final boolean includeProd;

    @Override
    public Flux<TpsIdentStatusDTO> call() {

        return webClient.get()
                .uri(builder -> builder.path(TPS_MESSAGING_URL)
                        .queryParam(IDENTER, idents)
                        .queryParamIfPresent(MILJOER, nonNull(envs) ? Optional.of(envs) : Optional.empty())
                        .queryParam(INCLUDE_PROD, includeProd)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(TpsIdentStatusDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(error -> log.error(error.getMessage(), error))
                .onErrorResume(error ->
                        Flux.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                error.getMessage(), error.getCause())));
    }
}
