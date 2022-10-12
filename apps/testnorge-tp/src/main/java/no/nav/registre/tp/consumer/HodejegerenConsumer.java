package no.nav.registre.tp.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.tp.consumer.command.GetLevendeIdenterCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Component
@Slf4j
public class HodejegerenConsumer {

    private final WebClient webClient;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl,
                               ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.webClient = WebClient.builder()
                .baseUrl(hodejegerenServerUrl)
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(value = "tp.resource.latency", extraTags = {"operation", "hodejegeren"})
    public List<String> getLevende(
            Long avspillergruppeId
    ) {
        return new GetLevendeIdenterCommand(avspillergruppeId, webClient).call();
    }

}
