package no.nav.testnav.apps.organisasjontilgangservice.client.maskinporten.v1.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.organisasjontilgangservice.client.maskinporten.v1.dto.AccessToken;
import no.nav.testnav.apps.organisasjontilgangservice.client.maskinporten.v1.dto.WellKnown;

@Slf4j
@RequiredArgsConstructor
public class GetAccessTokenCommand implements Callable<Mono<AccessToken>> {
    private final WebClient webClient;
    private final WellKnown wellKnown;
    private final String assertion;

    @Override
    public Mono<AccessToken> call() {
        return webClient.post()
                .uri(wellKnown.tokenEndpoint())
                .body(BodyInserters
                        .fromFormData("grant_type", wellKnown.grantTypesSupported().get(0))
                        .with("assertion", assertion)
                )
                .retrieve()
                .bodyToMono(AccessToken.class)
                .doOnSuccess(value -> log.info("AccessToken hentet fra maskinporten."))
                .doOnError(
                        throwable -> throwable instanceof WebClientResponseException,
                        throwable -> log.error(
                                "Feil ved henting av AccessToken for maskinporten. \n{}",
                                ((WebClientResponseException) throwable).getResponseBodyAsString()
                        )
                );
    }
}
