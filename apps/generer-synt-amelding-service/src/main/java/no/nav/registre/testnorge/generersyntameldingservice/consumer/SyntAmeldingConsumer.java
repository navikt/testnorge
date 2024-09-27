package no.nav.registre.testnorge.generersyntameldingservice.consumer;

import no.nav.registre.testnorge.generersyntameldingservice.config.Consumers;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostHentArbeidsforholdCommand;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostHentHistorikkCommand;
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
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public SyntAmeldingConsumer(TokenExchange tokenExchange,
                                Consumers consumers,
                                WebClient.Builder webClientBuilder) {

        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getSyntAmelding();
        this.webClient = webClientBuilder
                .exchangeStrategies(
                        ExchangeStrategies
                                .builder()
                                .codecs(configurer -> configurer
                                        .defaultCodecs()
                                        .maxInMemorySize(16 * 1024 * 1024))
                                .build())
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Arbeidsforhold getEnkeltArbeidsforhold(ArbeidsforholdPeriode periode, ArbeidsforholdType arbeidsforholdType) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new PostHentArbeidsforholdCommand(
                        webClient, periode, arbeidsforholdType.getPath(), accessToken.getTokenValue()).call())
                .block();
    }

    public List<Arbeidsforhold> getHistorikk(Arbeidsforhold arbeidsforhold) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new PostHentHistorikkCommand(
                        webClient, arbeidsforhold, accessToken.getTokenValue()).call())
                .block();
    }
}
