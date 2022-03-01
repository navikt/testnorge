package no.nav.registre.bisys.consumer;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.bisys.consumer.command.GetSyntBisysMeldingerCommand;
import no.nav.registre.bisys.consumer.credential.SyntBisysProperties;
import no.nav.registre.bisys.consumer.responses.SyntetisertBidragsmelding;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class SyntBisysConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntBisysConsumer(
            SyntBisysProperties syntProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = syntProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(value = "bisys.resource.latency", extraTags = {"operation", "bisys-syntetisereren"})
    public List<SyntetisertBidragsmelding> getSyntetiserteBidragsmeldinger(int antallMeldinger) {
        var token = tokenExchange.exchange(serviceProperties).block().getTokenValue();
        return new GetSyntBisysMeldingerCommand(antallMeldinger, token, webClient).call();
    }
}
