package no.nav.testnav.apps.tpservice.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpservice.consumer.command.hodejegeren.GetAlleLevendeCommand;
import no.nav.testnav.apps.tpservice.consumer.command.hodejegeren.GetLevendeIdenterCommand;
import no.nav.testnav.apps.tpservice.consumer.command.hodejegeren.PostSaveHistorikkCommand;
import no.nav.testnav.apps.tpservice.domain.TpSaveInHodejegerenRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class HodejegerenConsumer {

    private final WebClient webClient;

    public HodejegerenConsumer(
            @Value("${consumers.testnorge-hodejegeren.url}") String hodejegerenServerUrl,
            ExchangeFilterFunction metricsWebClientFilterFunction
    ) {

        this.webClient = WebClient.builder()
                .baseUrl(hodejegerenServerUrl)
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(value = "tp.resource.latency", extraTags = {"operation", "hodejegeren"})
    public List<String> saveHistory(
            TpSaveInHodejegerenRequest request
    ) {
        try {
            return new PostSaveHistorikkCommand(request, webClient).call();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<String> getLevende(
            Long avspillergruppeId
    ) {
        return new GetAlleLevendeCommand(avspillergruppeId, webClient).call();
    }

    public List<String> getLevende(
            Long avspillergruppeId,
            String miljoe,
            int antallIdenter,
            int minAlder
    ) {
        return new GetLevendeIdenterCommand(avspillergruppeId, miljoe, antallIdenter, minAlder, webClient).call();
    }

}
