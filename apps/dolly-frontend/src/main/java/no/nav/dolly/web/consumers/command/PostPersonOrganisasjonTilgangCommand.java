package no.nav.dolly.web.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.consumers.dto.AltinnBrukerRequest;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
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
                .headers(WebClientHeader.bearer(token))
                .bodyValue(new AltinnBrukerRequest(ident))
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .doOnError(throwable -> {
                    var description = WebClientError.describe(throwable);
                    log.error("Feilet å hente organisasjon, status: {}, feilmelding: {}",
                            description.getStatus(),
                            description.getMessage(),
                            throwable);
                })
                .retryWhen(WebClientError.is5xxException());
    }

}
