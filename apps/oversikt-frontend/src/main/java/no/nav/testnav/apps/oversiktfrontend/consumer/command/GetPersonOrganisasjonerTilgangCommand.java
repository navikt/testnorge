package no.nav.testnav.apps.oversiktfrontend.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetPersonOrganisasjonerTilgangCommand implements Callable<Flux<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String token;

    @Override
    public Flux<OrganisasjonDTO> call() {
        return webClient
                .get()
                .uri("/api/v1/person/organisasjoner")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}