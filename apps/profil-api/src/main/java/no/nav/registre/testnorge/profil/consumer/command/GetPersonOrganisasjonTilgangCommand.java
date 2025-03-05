package no.nav.registre.testnorge.profil.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.profil.consumer.dto.AltinnRequestDTO;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
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
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
