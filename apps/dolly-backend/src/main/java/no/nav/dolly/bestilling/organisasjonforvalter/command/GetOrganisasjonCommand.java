package no.nav.dolly.bestilling.organisasjonforvalter.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDetaljer;
import no.nav.dolly.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<Flux<OrganisasjonDetaljer>> {

    private static final String ORGANISASJON_FORVALTER_URL = "/api/v2/organisasjoner";

    private final WebClient webClient;
    private final List<String> orgnumre;
    private final String token;


    @Override
    public Flux<OrganisasjonDetaljer> call() {

        return webClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(ORGANISASJON_FORVALTER_URL)
                                .queryParam("orgnumre", orgnumre)
                                .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(OrganisasjonDetaljer.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
