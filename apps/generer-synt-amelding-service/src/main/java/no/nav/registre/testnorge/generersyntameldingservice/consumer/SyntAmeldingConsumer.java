package no.nav.registre.testnorge.generersyntameldingservice.consumer;

import no.nav.registre.testnorge.generersyntameldingservice.consumer.credentials.SyntAmeldingProperties;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostArbeidsforholdCommand;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostHistorikkCommand;
import no.nav.registre.testnorge.generersyntameldingservice.domain.ArbeidsforholdType;
import no.nav.testnav.libs.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class SyntAmeldingConsumer {

    private final WebClient webClient;
    private final ServerProperties properties;
    private final TokenExchange tokenExchange;

    public SyntAmeldingConsumer(TokenExchange tokenExchange, SyntAmeldingProperties properties) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(properties.getUrl())
                .build();
    }

    public Arbeidsforhold getEnkeltArbeidsforhold(ArbeidsforholdPeriode periode, ArbeidsforholdType arbeidsforholdType) {
        var accessToken = tokenExchange.exchange(properties).block();
        return new PostArbeidsforholdCommand(periode, webClient, arbeidsforholdType.getPath(), accessToken.getTokenValue()).call();
    }

    public List<Arbeidsforhold> getHistorikk(Arbeidsforhold arbeidsforhold) {
        var accessToken = tokenExchange.exchange(properties).block();
        return new PostHistorikkCommand(webClient, arbeidsforhold, accessToken.getTokenValue()).call();
    }
}
