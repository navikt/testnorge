package no.nav.registre.sdforvalter.consumer.rs.kodeverk;

import no.nav.registre.sdforvalter.config.Consumers;
import no.nav.registre.sdforvalter.consumer.rs.kodeverk.command.GetYrkerKodeverkCommand;
import no.nav.registre.sdforvalter.consumer.rs.kodeverk.response.KodeverkResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KodeverkConsumer {
    private final WebClient webClient;

    public KodeverkConsumer(
            Consumers consumers,
            WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(consumers
                        .getKodeverkApi()
                        .getUrl())
                .build();
    }

    public KodeverkResponse getYrkeskoder() {
        return new GetYrkerKodeverkCommand(webClient).call();
    }
}
