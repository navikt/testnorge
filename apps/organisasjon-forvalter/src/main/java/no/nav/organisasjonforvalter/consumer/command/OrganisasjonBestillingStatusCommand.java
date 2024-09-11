package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.dto.responses.StatusDTO;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class OrganisasjonBestillingStatusCommand implements Callable<Mono<BestillingStatus>> {

    private static final String STATUS_URL = "/api/v2/order/{uuid}/ids/{id}/status";

    private final WebClient webClient;
    private final Status status;
    private final String token;

    @Override
    public Mono<BestillingStatus> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(STATUS_URL)
                        .build(status.getUuid(), status.getBestId()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(StatusDTO.class)
                .flatMap(value -> Mono.just(BestillingStatus.builder()
                        .orgnummer(status.getOrganisasjonsnummer())
                        .uuid(status.getUuid())
                        .miljoe(status.getMiljoe())
                        .status(value)
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(throwable ->
                        Mono.just(BestillingStatus.builder()
                                .orgnummer(status.getOrganisasjonsnummer())
                                .uuid(status.getUuid())
                                .miljoe(status.getMiljoe())
                                .feilmelding(WebClientFilter.getMessage(throwable))
                                .build()));
    }
}