package no.nav.registre.skd.consumer;

import no.nav.registre.skd.consumer.command.PostTpIdenterCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class TpConsumer {

    private final WebClient webClient;

    public TpConsumer(@Value("${testnorge-tp.rest-api.url}") String serverUrl,
                      ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public List<String> leggTilIdenterITp(List<String> identer, String miljoe) {
        return new PostTpIdenterCommand(identer, miljoe, webClient).call();
    }
}
