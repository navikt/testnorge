package no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.dto.DeleteStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class DeleteOrganisasjonAccessCommand implements Callable<Mono<DeleteStatus>> {
    private final WebClient webClient;
    private final String token;
    private final String apiKey;
    private final Integer id;


    @Override
    public Mono<DeleteStatus> call() {

        return webClient
                .delete()
                .uri(builder -> builder.path("/api/serviceowner/Srr/{id}")
                        .build(id)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("ApiKey", apiKey)
                .header(HttpHeaders.CONTENT_TYPE, "application/hal+json")
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> DeleteStatus.builder()
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .build())
                .doOnSuccess(value -> log.info("Organiasjon tilgang {} slettet.", id))
                .doOnError(
                        throwable -> throwable instanceof WebClientResponseException,
                        throwable -> log.error(
                                "Feil ved henting av rettigheter i Altinn. {}",
                                ((WebClientResponseException) throwable).getResponseBodyAsString()));
    }
}
