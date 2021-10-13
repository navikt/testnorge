package no.nav.registre.endringsmeldinger.consumer.rs;

import no.nav.registre.endringsmeldinger.consumer.rs.command.GetLevendeIdenterCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class HodejegerenConsumer {

    private final WebClient webClient;

    public HodejegerenConsumer(@Value("${consumers.hodejegeren.url}") String hodejegerenServerUrl) {
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(hodejegerenServerUrl)
                .build();
    }

    public List<String> getLevende(Long avspillergruppeId){
        return new GetLevendeIdenterCommand(avspillergruppeId, webClient).call();
    }
}
