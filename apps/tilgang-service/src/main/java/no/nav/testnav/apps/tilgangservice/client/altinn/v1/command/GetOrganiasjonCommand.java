package no.nav.testnav.apps.tilgangservice.client.altinn.v1.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.tilgangservice.client.altinn.v1.dto.OrganisajonDTO;


@RequiredArgsConstructor
public class GetOrganiasjonCommand implements Callable<Mono<OrganisajonDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String organisajonsnummer;
    private final String apiKey;

    @Override
    public Mono<OrganisajonDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("api/serviceowner/organizations/{organisajonsnummer}").build(organisajonsnummer)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("ApiKey", apiKey)
                .retrieve()
                .bodyToMono(OrganisajonDTO.class);
    }
}
