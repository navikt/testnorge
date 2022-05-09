package no.nav.registre.bisys.consumer;

import no.nav.registre.bisys.consumer.command.OpprettBidragsmeldingCommand;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.bisys.consumer.command.OpprettBidragsmeldingCommand;
import no.nav.registre.bisys.consumer.response.SyntetisertBidragsmelding;

@Component
public class BidragsmeldingConsumer {

    private final WebClient webClient;

    public BidragsmeldingConsumer(@Value("${consumers.bidragsmelding.url}") String url,
                                  ExchangeFilterFunction metricsWebClientFilterFunction) {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public void opprett(List<SyntetisertBidragsmelding> list, String miljoe) {
        list.forEach(item -> new OpprettBidragsmeldingCommand(webClient, item).run());
    }
}