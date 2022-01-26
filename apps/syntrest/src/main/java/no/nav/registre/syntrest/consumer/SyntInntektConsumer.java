package no.nav.registre.syntrest.consumer;

import no.nav.registre.syntrest.consumer.command.PostSyntInntektCommand;
import no.nav.registre.syntrest.domain.inntekt.Inntektsmelding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class SyntInntektConsumer {

    private final WebClient webClient;

    public SyntInntektConsumer(
            @Value("${synth-inntekt-url}") String inntektUrl
    ) {
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(inntektUrl)
                .build();
    }

    public Map<String, List<Inntektsmelding>> synthesizeData(
            Map<String, List<Inntektsmelding>> fnrInntektMap
    ){
        return new PostSyntInntektCommand(fnrInntektMap, webClient).call();
    }
}
