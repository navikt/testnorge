package no.nav.identpool.consumers.command;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.dto.TpsIdentStatusDTO;
import no.nav.identpool.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class TpsMessagingGetCommand implements Callable<Flux<TpsIdentStatusDTO>> {

    private static final String TPS_MESSAGING_URL = "/api/v1/identer";
    private static final String IDENTER = "identer";
    private static final String INCLUDE_PROD = "includeProd";

    private final WebClient webClient;
    private final String token;
    private final List<String> identer;
    private final boolean includeProd;

    @Override
    public Flux<TpsIdentStatusDTO> call() {

        return webClient.get()
                .uri(builder -> builder.path(TPS_MESSAGING_URL)
                        .queryParam(IDENTER, identer)
                        .queryParam(INCLUDE_PROD, includeProd)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(TpsIdentStatusDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
