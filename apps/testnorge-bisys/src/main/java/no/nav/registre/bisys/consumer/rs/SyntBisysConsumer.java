package no.nav.registre.bisys.consumer.rs;

import java.util.List;

import no.nav.registre.bisys.consumer.rs.command.GetSyntBisysMeldingerCommand;
import no.nav.registre.bisys.consumer.rs.credential.SyntBisysProperties;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;

@Component
public class SyntBisysConsumer {

    private final AccessTokenService tokenService;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntBisysConsumer(
            SyntBisysProperties syntProperties,
            AccessTokenService accessTokenService
    ) {
        this.serviceProperties = syntProperties;
        this.tokenService = accessTokenService;
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
        var token = tokenService.generateClientCredentialAccessToken(serviceProperties).block().getTokenValue();
        return new GetSyntBisysMeldingerCommand(antallMeldinger, token, webClient).call();
    }
}
