package no.nav.testnav.apps.oversiktfrontend.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.oversiktfrontend.consumer.dto.OrganisasjonDTO;

@RequiredArgsConstructor
public class GetPersonOrganisasjonerTilgangCommand implements Callable<Flux<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String token;

    @Override
    public Flux<OrganisasjonDTO> call() {
        return webClient
                .get()
                .uri("/api/v1/person/organisasjoner")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class);
    }
}