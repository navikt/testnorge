package no.nav.registre.skd.consumer;


import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.commands.HodejegerenHentRelasjonerCommand;
import no.nav.registre.skd.commands.HodejegerenStatusQuoCommand;
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
        return new HodejegerenStatusQuoCommand(webClient, ident, endringskode, miljoe).call();
    }

    public RelasjonsResponse getRelasjoner(String fnr, String miljoe) {
        return new HodejegerenHentRelasjonerCommand(webClient, fnr, miljoe).call();
    }
}
