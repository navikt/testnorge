package no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.dto.AccessToken;
import no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.dto.WellKnown;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

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
                        .fromFormData("grant_type", wellKnown.grantTypesSupported().getFirst())
                        .with("assertion", assertion)
                )
                .retrieve()
                .bodyToMono(AccessToken.class)
                .doOnSuccess(value -> log.info("AccessToken hentet fra maskinporten."))
                .doOnError(WebClientError.logTo(log))
                .cache(Duration.ofSeconds(10L));
    }
}
