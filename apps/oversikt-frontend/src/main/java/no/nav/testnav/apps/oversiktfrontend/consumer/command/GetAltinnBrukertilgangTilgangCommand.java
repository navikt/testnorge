package no.nav.testnav.apps.oversiktfrontend.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.AltinnBrukerRequest;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetAltinnBrukertilgangTilgangCommand implements Callable<Flux<OrganisasjonDTO>> {

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO> call() {
        return webClient
                .post()
                .uri("/api/v1/brukertilgang")
                .headers(WebClientHeader.bearer(token))
                .bodyValue(new AltinnBrukerRequest(ident))
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .retryWhen(WebClientError.is5xxException());
    }

}