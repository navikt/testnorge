package no.nav.registre.testnorge.profil.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.profil.consumer.dto.OrganisasjonDTO;

@RequiredArgsConstructor
public class GetPersonOrganisasjonTilgangCommand implements Callable<Mono<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String organisasjonsnummer;

    @Override
    public Mono<OrganisasjonDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/v1/person/organisasjoner/{organisasjonsnummer}").build(organisasjonsnummer))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(OrganisasjonDTO.class);
    }
}
