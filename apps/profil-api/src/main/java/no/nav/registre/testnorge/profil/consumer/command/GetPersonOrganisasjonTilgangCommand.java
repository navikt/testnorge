package no.nav.registre.testnorge.profil.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.profil.consumer.dto.AltinnRequestDTO;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(new AltinnRequestDTO(ident))
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException());
    }

}
