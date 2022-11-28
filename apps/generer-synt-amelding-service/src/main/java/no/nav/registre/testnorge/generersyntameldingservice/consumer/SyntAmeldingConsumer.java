package no.nav.registre.testnorge.generersyntameldingservice.consumer;

import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostHentArbeidsforholdCommand;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostHentHistorikkCommand;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.credentials.SyntAmeldingProperties;
import no.nav.registre.testnorge.generersyntameldingservice.domain.ArbeidsforholdType;
import no.nav.testnav.libs.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class SyntAmeldingConsumer {

    private final WebClient webClient;
    private final ServerProperties properties;
    private final TokenExchange tokenExchange;

    public SyntAmeldingConsumer(TokenExchange tokenExchange,
                                SyntAmeldingProperties properties,
                                ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(properties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Arbeidsforhold getEnkeltArbeidsforhold(ArbeidsforholdPeriode periode, ArbeidsforholdType arbeidsforholdType) {
        return tokenExchange.exchange(properties)
                .flatMap(accessToken -> new PostHentArbeidsforholdCommand(
                        webClient, periode, arbeidsforholdType.getPath(), accessToken.getTokenValue()).call())
                .block();
    }

    public List<Arbeidsforhold> getHistorikk(Arbeidsforhold arbeidsforhold) {
        return tokenExchange.exchange(properties)
                .flatMap(accessToken -> new PostHentHistorikkCommand(
                        webClient, arbeidsforhold, accessToken.getTokenValue()).call())
                .block();
    }
}
