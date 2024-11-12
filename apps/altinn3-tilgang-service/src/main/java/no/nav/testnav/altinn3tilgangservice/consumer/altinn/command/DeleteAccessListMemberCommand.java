package no.nav.testnav.altinn3tilgangservice.consumer.altinn.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.config.AltinnConfig;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.DeleteStatus;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class DeleteAccessListMemberCommand implements Callable<Mono<DeleteStatus>> {

    private static final String ALTINN_URL = "/access-lists/{owner}/{identifier}/members";
    private final WebClient webClient;
    private final String token;
    private final String identifiers;
    private final AltinnConfig altinnConfig;


    @Override
    public Mono<DeleteStatus> call() {

        return webClient
                .method(HttpMethod.DELETE)
                .uri(builder -> builder.path(ALTINN_URL)
                        .build(altinnConfig.getOwner(), altinnConfig.getIdentifier())
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(identifiers)
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> DeleteStatus.builder()
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .build())
                .doOnSuccess(value -> log.info("Organisasjontilgang slettet for {}", identifiers))
                .doOnError(WebClientFilter::logErrorMessage);
    }
}
