package no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.config.MaskinportenConfig;
import no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.dto.WellKnown;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetWellKnownCommand implements Callable<Mono<WellKnown>> {

    private final WebClient webClient;
    private final MaskinportenConfig maskinportenConfig;

    @Override
    public Mono<WellKnown> call() {

        return webClient.get()
                .uri(maskinportenConfig.getMaskinportenWellKnownUrl())
                .retrieve()
                .bodyToMono(WellKnown.class)
                .doOnSuccess(value -> log.info("WellKnown hentet for maskinporten."))
                .doOnError(WebClientError.logTo(log))
                .cache(Duration.ofDays(1L));
    }
}
