package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class OrganisasjonBestillingIdsCommand implements Callable<Mono<Status>> {

    private static final String STATUS_URL = "/api/v2/order/{uuid}/ids";

    private final WebClient webClient;
    private final Status status;
    private final String token;

    @Override
    public Mono<Status> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(STATUS_URL)
                        .build(status.getUuid()))
                .headers(WebClientHeader.bearer(token))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String[].class)
                .doOnNext(resultat -> log.info("Melding mottatt {}", Arrays.toString(resultat)))
                .map(resultat -> Arrays.stream(resultat)
                                .mapToInt(Integer::parseInt)
                                .max()
                                .orElse(0))
                .doOnNext(resultat -> log.info("Index funnet {}", resultat))
                .map(index -> Status.builder()
                        .id(status.getId())
                        .bestId(index != 0 ? index.toString() : null)
                        .uuid(status.getUuid())
                        .miljoe(status.getMiljoe())
                        .organisasjonsnummer(status.getOrganisasjonsnummer())
                        .build())
                .doOnError(WebClientError.logTo(log));
    }
}