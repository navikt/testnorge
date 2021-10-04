package no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.dto.RightDTO;

@Slf4j
@RequiredArgsConstructor
public class GetRightsCommand implements Callable<Flux<RightDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String serviceCode;
    private final String serviceEdition;
    private final String apiKey;

    @Override
    public Flux<RightDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/serviceowner/Srr")
                        .queryParam("serviceCode", serviceCode)
                        .queryParam("serviceEditionCode", serviceEdition)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("ApiKey", apiKey)
                .retrieve()
                .bodyToFlux(RightDTO.class)
                .doOnError(
                        throwable -> throwable instanceof WebClientResponseException,
                        throwable -> log.error(
                                "Feil ved henting av rettigheter i Altinn. \n{}",
                                ((WebClientResponseException) throwable).getResponseBodyAsString()
                        )
                );
    }
}