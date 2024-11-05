package no.nav.testnav.altinn3tilgangservice.consumer.altinn.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AltinnResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetAccessListMembersCommand implements Callable<Flux<AltinnResponseDTO>> {

    private static final String ALTINN_URL = "/access-lists/{owner}/{identifier}/members";

    private final WebClient webClient;
    private final String token;
    private final String owner;
    private final String identifier;

    @Override
    public Flux<AltinnResponseDTO> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(ALTINN_URL)
                        .build(owner, identifier)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(AltinnResponseDTO.class)
                .doOnError(
                        WebClientResponseException.class::isInstance,
                        throwable -> log.error(
                                "Feil ved henting av rettigheter i Altinn. {}",
                                ((WebClientResponseException) throwable).getResponseBodyAsString()))
                ;
    }
}