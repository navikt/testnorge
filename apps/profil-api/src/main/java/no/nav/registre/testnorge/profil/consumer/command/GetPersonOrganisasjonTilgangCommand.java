package no.nav.registre.testnorge.profil.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.profil.consumer.dto.AltinnRequestDTO;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class GetPersonOrganisasjonTilgangCommand implements Callable<Flux<OrganisasjonDTO>> {

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<OrganisasjonDTO> call() {
        return webClient
                .post()
                .uri(builder -> builder.path("/api/v1/brukertilgang")
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(new AltinnRequestDTO(ident))
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
