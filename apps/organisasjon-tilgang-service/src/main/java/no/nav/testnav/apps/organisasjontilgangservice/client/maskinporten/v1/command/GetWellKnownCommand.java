package no.nav.testnav.apps.organisasjontilgangservice.client.maskinporten.v1.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.organisasjontilgangservice.config.MaskinportenConfig;
import no.nav.testnav.apps.organisasjontilgangservice.client.maskinporten.v1.dto.WellKnown;

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
                        throwable -> throwable instanceof WebClientResponseException,
                        throwable -> log.error(
                                "Feil ved  henting av well known for maskinporten. \n{}",
                                ((WebClientResponseException) throwable).getResponseBodyAsString()
                        )
                );
    }
}
