package no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.config.MaskinportenConfig;
import no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.dto.WellKnown;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetWellKnownCommand implements Callable<Mono<WellKnown>> {
    private final WebClient webClient;
    private final MaskinportenConfig maskinportenConfig;

    @Override
    public Mono<WellKnown> call() {
        return webClient.get()
                .uri(maskinportenConfig.getWellKnownUrl())
                .retrieve()
                .bodyToMono(WellKnown.class)
                .doOnSuccess(value -> log.info("WellKnown hentet for maskinporten."))
                .doOnError(
                        WebClientResponseException.class::isInstance,
                        throwable -> log.error(
                                "Feil ved  henting av well known for maskinporten. \n{}",
                                ((WebClientResponseException) throwable).getResponseBodyAsString()
                        )
                );
    }
}
