package no.nav.testnav.altinn3tilgangservice.consumer.altinn.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.OrganisasjonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
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
                .bodyToMono(OrganisasjonDTO.class)
                .doOnError(
                        WebClientResponseException.class::isInstance,
                        throwable -> log.error(
                                "Feil ved henting av rettigheter i Altinn. {}",
                                ((WebClientResponseException) throwable).getResponseBodyAsString()));
    }
}
