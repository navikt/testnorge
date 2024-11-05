package no.nav.testnav.altinn3tilgangservice.consumer.altinn.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.OrganisasjonDataDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.RightDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class CreateAccessListeMemberCommand implements Callable<Mono<RightDTO>> {

    private static final String ALTINN_URL = "/access-lists/{owner}/{identifier}/members";

    private final WebClient webClient;
    private final String token;
    private final OrganisasjonDataDTO body;
    private final String owner;
    private final String identifier;


    @Override
    public Mono<RightDTO> call() {

        return webClient
                .post()
                .uri(builder -> builder.path(ALTINN_URL)
                        .build(owner, identifier)
                )
                .bodyValue(body)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(RightDTO[].class)
                .map(list -> list[0])
                .doOnError(
                        WebClientResponseException.class::isInstance,
                        throwable -> log.error(
                                "Feil ved opprettelse av organisasjon tilgang i Altinn. {}",
                                ((WebClientResponseException) throwable).getResponseBodyAsString()
                        )
                );
    }
}