package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.command.GetAlleIdenterCommand;
import no.nav.registre.sdforvalter.consumer.rs.command.GetLevendeIdenterCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
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

    /**
     * @param playgroupId AvspillergruppeId som man ønsker å hente fnr fra
     * @return En liste med fnr som eksisterer i gruppen
     */
    public List<String> getPlaygroupFnrs(Long playgroupId) {
        return new GetAlleIdenterCommand(playgroupId, webClient).call();
    }

    public List<String> getLivingFnrs(Long playgroupId, String environment) {
        return new GetLevendeIdenterCommand(playgroupId, environment, webClient).call();
    }

}