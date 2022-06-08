package no.nav.testnav.apps.brukerservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.brukerservice.consumer.dto.OrganisasjonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<Mono<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String organisasjonsnummer;
    private final String token;

    @Override
    public Mono<OrganisasjonDTO> call() {
        return webClient.get()
                .uri(builder -> builder.path("/api/v1/person/organisasjoner/{organisasjonsnummer}").build(organisasjonsnummer))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(OrganisasjonDTO.class)
                .onErrorResume(
                        throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty()
                );
    }
}

