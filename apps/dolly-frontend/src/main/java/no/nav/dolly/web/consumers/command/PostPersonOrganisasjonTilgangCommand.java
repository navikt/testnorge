package no.nav.dolly.web.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.consumers.dto.AltinnBrukerRequest;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PostPersonOrganisasjonTilgangCommand implements Callable<Flux<OrganisasjonDTO>> {

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<OrganisasjonDTO> call() {
        return webClient
                .post()
                .uri(builder -> builder.path("/api/v1/brukertilgang").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(new AltinnBrukerRequest(ident))
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .doOnError(error -> log.error("Feilet å hente organisasjon, status: {}, feilmelding: {}",
                        WebClientError.describe(error).getStatus(),
                        WebClientError.describe(error).getMessage(),
                        error))
                .retryWhen(WebClientError.is5xxException());
    }

}
