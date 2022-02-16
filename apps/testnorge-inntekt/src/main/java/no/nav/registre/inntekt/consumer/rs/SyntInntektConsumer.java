package no.nav.registre.inntekt.consumer.rs;

import no.nav.registre.inntekt.consumer.rs.command.PostSyntInntektCommand;
import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

@Component
public class SyntInntektConsumer {

    private final WebClient webClient;

    public SyntInntektConsumer(
            @Value("${consumers.synt-inntekt.url}") String inntektUrl
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

    public SortedMap<String, List<RsInntekt>> hentSyntetiserteInntektsmeldinger(
            Map<String, List<RsInntekt>> identerMedInntekt
    ) {
        return new PostSyntInntektCommand(identerMedInntekt, webClient).call();
    }
}
