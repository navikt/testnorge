package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.dto.responses.StatusDTO;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class OrganisasjonBestillingStatusCommand implements Callable<Mono<BestillingStatus>> {

    private static final String STATUS_URL = "/api/v2/order/{uuid}/ids/{id}/status";

    private final WebClient webClient;
    private final Status status;
    private final String token;

    private static String getError(Throwable throwable) {

        return throwable instanceof WebClientResponseException ?
                ((WebClientResponseException) throwable).getResponseBodyAsString() :
                throwable.getMessage();
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

                .doOnError(throwable -> {
                    log.error(getError(throwable));
                    Mono.just(BestillingStatus.builder()
                            .uuid(status.getUuid())
                            .miljoe(status.getMiljoe())
                            .feilmelding(getError(throwable))
                            .build());
                });
    }
}