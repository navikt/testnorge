package no.nav.registre.skd.commands;

import no.nav.registre.skd.consumer.response.RelasjonsResponse;
import org.springframework.web.reactive.function.client.WebClient;

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
                .block();
    }

}
