package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class OrganisasjonBestillingCommand implements Callable<Mono<BestillingStatus>> {

    private static final String STATUS_URL = "/api/v1/order/{uuid}/items";

    private final WebClient webClient;
    private final Status status;
    private final String token;

    @Override
    public Mono<BestillingStatus> call() {

        return webClient.get()
                .uri(STATUS_URL.replace("{uuid}", status.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .flatMap(response -> response
                        .bodyToMono(BestillingStatus.ItemDto[].class)
                        .map(value -> BestillingStatus.builder()
                                .orgnummer(status.getOrganisasjonsnummer())
                                .uuid(status.getUuid())
                                .miljoe(status.getMiljoe())
                                .itemDtos(Arrays.asList(value))
                                .build()))

                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))

                .doOnError(throwable -> {
                    log.error(throwable instanceof WebClientResponseException ?
                            ((WebClientResponseException) throwable).getResponseBodyAsString() :
                            throwable.getMessage());

                    BestillingStatus.builder()
                            .uuid(status.getUuid())
                            .miljoe(status.getMiljoe())
                            .feilmelding(throwable instanceof WebClientResponseException ?
                                    ((WebClientResponseException) throwable).getResponseBodyAsString() :
                                    throwable.getMessage())
                            .build();
                });
    }
}