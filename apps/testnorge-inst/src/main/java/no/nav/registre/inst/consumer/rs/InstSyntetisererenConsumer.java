package no.nav.registre.inst.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.inst.consumer.rs.command.GetSyntInstMeldingerCommand;
import no.nav.registre.inst.consumer.rs.credential.SyntInstGcpProperties;
import no.nav.registre.inst.domain.InstitusjonsoppholdV2;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
@Slf4j
public class InstSyntetisererenConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public InstSyntetisererenConsumer(
            SyntInstGcpProperties syntInstGcpProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = syntInstGcpProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntInstGcpProperties.getUrl())
                .build();
    }


    @Timed(value = "inst.resource.latency", extraTags = {"operation", "inst-syntetisereren"})
    public List<InstitusjonsoppholdV2> hentInstMeldingerFromSyntRest(int numToGenerate) {
        var accessToken = tokenExchange.exchange(serviceProperties).block().getTokenValue();
        return new GetSyntInstMeldingerCommand(numToGenerate, accessToken, webClient).call();
    }
}
