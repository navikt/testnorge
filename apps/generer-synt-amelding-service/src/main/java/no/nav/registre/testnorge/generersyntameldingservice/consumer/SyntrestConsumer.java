package no.nav.registre.testnorge.generersyntameldingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.testnorge.generersyntameldingservice.config.credentials.SyntrestProperties;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostArbeidsforholdCommand;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostHistorikkCommand;
import no.nav.registre.testnorge.generersyntameldingservice.domain.ArbeidsforholdType;
import no.nav.testnav.libs.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class SyntrestConsumer {

    private final WebClient webClient;
    private final ServerProperties properties;
    private final TokenExchange tokenExchange;

    public SyntrestConsumer(TokenExchange tokenExchange, SyntrestProperties properties) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    public Arbeidsforhold getEnkeltArbeidsforhold(ArbeidsforholdPeriode periode, ArbeidsforholdType arbeidsforholdType) {
        var accessToken = tokenExchange.generateToken(properties).block();
        return new PostArbeidsforholdCommand(periode, webClient, arbeidsforholdType.getPath(), accessToken.getTokenValue()).call();
    }

    public List<Arbeidsforhold> getHistorikk(Arbeidsforhold arbeidsforhold) {
        var accessToken = tokenExchange.generateToken(properties).block();
        return new PostHistorikkCommand(webClient, arbeidsforhold, accessToken.getTokenValue()).call();
    }
}
