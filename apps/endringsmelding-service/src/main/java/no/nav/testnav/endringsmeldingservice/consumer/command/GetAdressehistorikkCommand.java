package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.AdressehistorikkRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetAdressehistorikkCommand implements Callable<Flux<AdressehistorikkDTO>> {

    private static final String ADRESSE_HIST_URL = "/api/v1/personer/adressehistorikk";
    private static final String MILJOER = "miljoer";

    private final WebClient webClient;
    private final AdressehistorikkRequest request;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Flux<AdressehistorikkDTO> call() {

        return webClient
                .post()
                .uri(builder -> builder.path(ADRESSE_HIST_URL)
                        .queryParam(MILJOER, miljoer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToFlux(AdressehistorikkDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage);
    }
}
