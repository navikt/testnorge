package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GetLevendeIdenterCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class HodejegerenConsumer {

    private final WebClient webClient;

    public HodejegerenConsumer(
            @Value("${consumers.hodejegeren.url}") String url,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Flux<String> getIdenter(String miljo, int antall) {
        return new GetLevendeIdenterCommand(webClient, miljo, antall).call();
    }
}
