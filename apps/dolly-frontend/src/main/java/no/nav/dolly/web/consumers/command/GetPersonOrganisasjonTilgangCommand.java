package no.nav.dolly.web.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.consumers.dto.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetPersonOrganisasjonTilgangCommand implements Callable<Mono<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String organisasjonsnummer;

    @Override
    public Mono<OrganisasjonDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/v1/person/organisasjoner/{organisasjonsnummer}").build(organisasjonsnummer))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(OrganisasjonDTO.class)
                .doOnError(error -> log.error("Feilet å hente organisasjon, status: {}, feilmelding: ",
                        WebClientFilter.getMessage(error),
                        WebClientFilter.getMessage(error),
                        error))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
