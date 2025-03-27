package no.nav.testnav.apps.brukerservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.brukerservice.consumer.dto.AltinnBrukerRequest;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class GetBrukertilgangCommand implements Callable<Flux<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<OrganisasjonDTO> call() {
        return webClient.post()
                .uri(builder -> builder.path("/api/v1/brukertilgang").build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(new AltinnBrukerRequest(ident))
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(
                        throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty()
                );
    }
}