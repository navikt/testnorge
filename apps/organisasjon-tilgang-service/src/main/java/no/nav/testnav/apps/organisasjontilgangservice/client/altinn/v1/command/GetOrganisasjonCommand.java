package no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.dto.OrganisasjonDTO;


@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<Mono<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String organisajonsnummer;
    private final String apiKey;

    @Override
    public Mono<OrganisasjonDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("api/serviceowner/organizations/{organisajonsnummer}").build(organisajonsnummer)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("ApiKey", apiKey)
                .retrieve()
                .bodyToMono(OrganisasjonDTO.class);
    }
}
