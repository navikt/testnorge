package no.nav.registre.bisys.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.bisys.consumer.command.OpprettBidragsmeldingCommand;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;

@Component
public class BidragsmedlingConsumer {

    private final WebClient webClient;

    public BidragsmedlingConsumer(@Value("${consumers.bidragsmelding.url}") String url) {
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public void opprett(List<SyntetisertBidragsmelding> list){
        list.forEach(item -> new OpprettBidragsmeldingCommand(webClient, item).run());
    }
}