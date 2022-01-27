package no.nav.registre.syntrest.consumer;

import no.nav.registre.syntrest.consumer.command.PostSyntAaregCommand;
import no.nav.registre.syntrest.domain.aareg.Arbeidsforholdsmelding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class SyntAaregConsumer {

    private final WebClient webClient;

    public SyntAaregConsumer(
            @Value("${synth-aareg-url}") String aaregUrl
    ) {
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(aaregUrl)
                .build();
    }

    public List<Arbeidsforholdsmelding> synthesizeData(
            List<String> fnrs
    ) {
        return new PostSyntAaregCommand(fnrs, webClient).call();
    }
}

