package no.nav.registre.syntrest.consumer;

import no.nav.registre.syntrest.consumer.command.GetSyntMeldekortCommand;
import no.nav.registre.syntrest.consumer.command.GetSyntMeldekortMedArbeidstimerCommand;
import no.nav.registre.syntrest.consumer.credentials.SyntMeldekortProperties;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

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

    public List<String> getSyntheticMeldekort(String meldegruppe, int antall){
        var token = Objects.requireNonNull(tokenExchange.exchange(serviceProperties).block()).getTokenValue();
        return new GetSyntMeldekortCommand(antall, meldegruppe, token, webClient).call();
    }

    public List<String> getSyntheticMeldekort(String meldegruppe, int antall, String arbeidstimer){
        var token = Objects.requireNonNull(tokenExchange.exchange(serviceProperties).block()).getTokenValue();
        return new GetSyntMeldekortMedArbeidstimerCommand(antall, meldegruppe, arbeidstimer, token, webClient).call();
    }
}
