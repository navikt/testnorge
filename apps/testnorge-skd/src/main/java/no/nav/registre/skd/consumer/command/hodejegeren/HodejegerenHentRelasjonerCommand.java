package no.nav.registre.skd.consumer.command.hodejegeren;

import no.nav.registre.skd.consumer.response.RelasjonsResponse;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

public class HodejegerenHentRelasjonerCommand implements Callable<RelasjonsResponse> {

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;

    public HodejegerenHentRelasjonerCommand(WebClient webClient, String ident, String miljoe) {
        this.webClient = webClient;
        this.ident = ident;
        this.miljoe = miljoe;
    }

    @Override
    public RelasjonsResponse call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/relasjoner-til-ident")
                        .queryParam("ident", ident)
                        .queryParam("miljoe", miljoe)
                        .build())
                .retrieve()
                .bodyToMono(RelasjonsResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

}
