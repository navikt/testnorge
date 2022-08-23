package no.nav.testnav.apps.hodejegeren.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.hodejegeren.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTpsStatusPaaIdenterCommand implements Callable<String> {

    private final WebClient webClient;
    private final String aksjonsKode;
    private final int antallIdenter;
    private final String miljoe;
    private final String identer;

    @Override
    public String call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/serviceroutine/FS03-FDLISTER-DISKNAVN-M")
                        .queryParam("aksjonsKode", aksjonsKode)
                        .queryParam("antallFnr", antallIdenter)
                        .queryParam("environment", miljoe)
                        .queryParam("nFnr", identer)
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

    }
}
