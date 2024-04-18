package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.IdentMiljoeDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetEksistererPersonCommand implements Callable<Flux<IdentMiljoeDTO>> {

    private static final String PERSON_DATA_URL = "/api/v1/identer";
    private static final String MILJOER = "miljoer";
    private static final String IDENTER = "identer";

    private static final String INCLUDE_PROD = "includeProd";

    private final WebClient webClient;
    private final Set<String> identer;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Flux<IdentMiljoeDTO> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(PERSON_DATA_URL)
                        .queryParam(IDENTER, identer)
                        .queryParamIfPresent(MILJOER, Optional.ofNullable(miljoer))
                        .queryParam(INCLUDE_PROD, false)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(IdentMiljoeDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage);
    }
}
