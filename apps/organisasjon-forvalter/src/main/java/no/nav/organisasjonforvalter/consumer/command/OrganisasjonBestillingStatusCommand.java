package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.dto.responses.StatusDTO;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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

    private static String getMessage(Throwable throwable) {

        return throwable instanceof WebClientResponseException ?
                ((WebClientResponseException) throwable).getResponseBodyAsString() :
                throwable.getMessage();
    }

    private static Mono<BestillingStatus> getError(Status status, String description) {

        return Mono.just(BestillingStatus.builder()
                .orgnummer(status.getOrganisasjonsnummer())
                .uuid(status.getUuid())
                .miljoe(status.getMiljoe())
                .feilmelding(description)
                .build());
    }

    @Override
    public Mono<BestillingStatus> call() {

        return webClient.get()
                .uri(STATUS_URL.replace("{uuid}", status.getUuid())
                        .replace("{id}", status.getBestId()))
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
                .doOnError(throwable -> log.error(getMessage(throwable)))
                .onErrorResume(throwable -> getError(status, getMessage(throwable)));
    }
}