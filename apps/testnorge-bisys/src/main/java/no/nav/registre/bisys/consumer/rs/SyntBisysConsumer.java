package no.nav.registre.bisys.consumer.rs;

import io.micrometer.core.annotation.Timed;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.bisys.consumer.rs.command.GetSyntBisysMeldingerCommand;
import no.nav.registre.bisys.consumer.rs.credential.SyntBisysProperties;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
public class SyntBisysConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntBisysConsumer(
            SyntBisysProperties syntProperties,
            TokenExchange tokenExchange
    ) {
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
        var token = tokenExchange.generateToken(serviceProperties).block().getTokenValue();
        return new GetSyntBisysMeldingerCommand(antallMeldinger, token, webClient).call();
    }
}
