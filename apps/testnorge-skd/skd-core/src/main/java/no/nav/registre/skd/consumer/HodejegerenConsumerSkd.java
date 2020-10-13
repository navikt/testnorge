package no.nav.registre.skd.consumer;


import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.commands.hodejegeren.HentDoedeOgUtvandredeCommand;
import no.nav.registre.skd.commands.hodejegeren.HentFoedteCommand;
import no.nav.registre.skd.commands.hodejegeren.HentGifteCommand;
import no.nav.registre.skd.commands.hodejegeren.HentLevendeCommand;
import no.nav.registre.skd.commands.hodejegeren.HentRelasjonerCommand;
import no.nav.registre.skd.commands.hodejegeren.StatusQuoCommand;
import no.nav.registre.skd.consumer.response.RelasjonsResponse;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
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

    public List<String> getLevende(Long avspillergruppeId) {
        return new HentLevendeCommand(webClient, avspillergruppeId).call();
    }

    public List<String> getFoedte(Long avspillergruppeId, Integer minimumAlder, Integer maksimumAlder) {
        return new HentFoedteCommand(webClient, avspillergruppeId, minimumAlder, maksimumAlder).call();
    }

    public List<String> getGifte(Long avspillergruppeId) {
        return new HentGifteCommand(webClient, avspillergruppeId).call();
    }

    public List<String> getDoedeOgUtvandrede(Long avspillergruppeId) {
        return new HentDoedeOgUtvandredeCommand(webClient, avspillergruppeId).call();
    }
}
