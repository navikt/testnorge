package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.dto.responses.StatusDTO;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .uri(STATUS_URL.replace("{uuid}", status.getUuid())
                        .replace("{id}", status.getBestId()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        clientResponse.createException().flatMap(error -> {

                            log.error(error.getResponseBodyAsString());
                            return Mono.just(BestillingStatus.builder()
                                    .uuid(status.getUuid())
                                    .miljoe(status.getMiljoe())
                                    .feilmelding(error.getMessage())
                                    .build());
                        });
                    }
                    return clientResponse.bodyToMono(StatusDTO.class)
                            .map(value -> BestillingStatus.builder()
                            .orgnummer(status.getOrganisasjonsnummer())
                            .uuid(status.getUuid())
                            .miljoe(status.getMiljoe())
                            .status(value)
                            .build());
                });
    }
}