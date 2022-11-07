package no.nav.registre.sdforvalter.consumer.rs;

import no.nav.registre.sdforvalter.config.credentials.KodeverkProperties;
import no.nav.registre.sdforvalter.consumer.rs.command.GetYrkerKodeverkCommand;
import no.nav.registre.sdforvalter.consumer.rs.response.KodeverkResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KodeverkConsumer {
    private final WebClient webClient;

    public KodeverkConsumer(
            KodeverkProperties serverProperties,
            ExchangeFilterFunction metricsWebClientFilterFunction
    ) {
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public KodeverkResponse getYrkeskoder() {
        return new GetYrkerKodeverkCommand(webClient).call();
    }
}
