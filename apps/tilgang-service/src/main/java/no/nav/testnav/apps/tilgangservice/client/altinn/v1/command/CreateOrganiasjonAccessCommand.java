package no.nav.testnav.apps.tilgangservice.client.altinn.v1.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.tilgangservice.client.altinn.v1.dto.RightDTO;

@Slf4j
@RequiredArgsConstructor
public class CreateOrganiasjonAccessCommand implements Callable<Mono<RightDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String apiKey;
    private final RightDTO dto;


    @Override
    public Mono<RightDTO> call() {
        return webClient
                .post()
                .uri(builder -> builder.path("/api/serviceowner/Srr")
                        .build()
                )
                .body(BodyInserters.fromPublisher(Mono.just(new RightDTO[]{dto}), RightDTO[].class))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("ApiKey", apiKey)
                .header(HttpHeaders.CONTENT_TYPE, "application/hal+json")
                .retrieve()
                .bodyToMono(RightDTO[].class)
                .map(list -> list[0])
                .doOnError(
                        throwable -> throwable instanceof WebClientResponseException,
                        throwable -> log.error(
                                "Feil ved opprettelse av organiasjon tilgang i Altinn. \n{}",
                                ((WebClientResponseException) throwable).getResponseBodyAsString()
                        )
                );
    }

}
