package no.nav.registre.bisys.consumer;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.bisys.consumer.command.GetSyntBisysMeldingerCommand;
import no.nav.registre.bisys.consumer.credential.SyntBisysProperties;
import no.nav.registre.bisys.consumer.response.SyntetisertBidragsmelding;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;

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
            TokenExchange tokenExchange) {

        this.serviceProperties = syntProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntProperties.getUrl())
                .build();
    }

    @Timed(value = "bisys.resource.latency", extraTags = {"operation", "bisys-syntetisereren"})
    public List<SyntetisertBidragsmelding> getSyntetiserteBidragsmeldinger(int antallMeldinger) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetSyntBisysMeldingerCommand(antallMeldinger, accessToken.getTokenValue(), webClient).call())
                .block();
    }
}
