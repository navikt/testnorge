package no.nav.registre.skd.consumer;


import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.commands.hodejegeren.HentRelasjonerCommand;
import no.nav.registre.skd.commands.hodejegeren.StatusQuoCommand;
import no.nav.registre.skd.consumer.response.RelasjonsResponse;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
public class HodejegerenConsumerSkd {
    private final WebClient webClient;

    public HodejegerenConsumerSkd(@Value("${testnorge-hodejegeren.rest-api.url}") String url) {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public Map<String, String> getStatusQuoTilhoerendeEndringskode(String endringskode, String miljoe, String ident) {
        return new StatusQuoCommand(webClient, ident, endringskode, miljoe).call();
    }

    public RelasjonsResponse getRelasjoner(String fnr, String miljoe) {
        return new HentRelasjonerCommand(webClient, fnr, miljoe).call();
    }
}
