package no.nav.testnav.apps.generersyntmeldekortservice.consumer;

import no.nav.testnav.apps.generersyntmeldekortservice.consumer.command.GetSyntMeldekortCommand;
import no.nav.testnav.apps.generersyntmeldekortservice.consumer.credential.SyntMeldekortProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class SyntMeldekortConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntMeldekortConsumer(
            SyntMeldekortProperties properties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = properties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(properties.getUrl())
                .build();
    }

    public Mono<List<String>> getSyntheticMeldekort(String meldegruppe, int antall, Double arbeidstimer) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetSyntMeldekortCommand(
                        antall, meldegruppe, arbeidstimer, accessToken.getTokenValue(), webClient).call());
    }
}
