package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class OrganisasjonServiceCommand implements Callable<Flux<OrganisasjonDTO>> {

    private static final String STATUS_URL = "/api/v1/organisasjoner/{orgnummer}";
    private static final String MILJOE = "miljo";

    private final WebClient webClient;
    private final String orgnummer;
    private final String environment;
    private final String token;

    @Override
    public Flux<OrganisasjonDTO> call() {

        log.info("Henter organisasjon {} fra miljø {} ", orgnummer, environment);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(STATUS_URL)
                        .build(orgnummer))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(MILJOE, environment)
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(throwable -> Mono.just(OrganisasjonDTO.builder()
                        .error(WebClientFilter.getMessage(throwable))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}