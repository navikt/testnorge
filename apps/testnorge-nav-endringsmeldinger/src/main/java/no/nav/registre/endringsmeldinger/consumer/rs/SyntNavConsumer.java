package no.nav.registre.endringsmeldinger.consumer.rs;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;

import java.util.List;

import no.nav.registre.endringsmeldinger.consumer.rs.command.GetSyntNavMeldingerCommand;
import no.nav.registre.endringsmeldinger.consumer.rs.credential.SyntNavProperties;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
public class SyntNavConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntNavConsumer(
            SyntNavProperties syntProperties,
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

    public List<Document> getSyntetiserteNavEndringsmeldinger(
            String endringskode,
            int antallMeldinger
    ) {
        var token = tokenExchange.exchange(serviceProperties).block().getTokenValue();
        return new GetSyntNavMeldingerCommand(endringskode, antallMeldinger, token, webClient).call();
    }
}
