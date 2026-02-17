package no.nav.testnav.levendearbeidsforholdansettelse.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.aareg.HentArbeidsforholdCommand;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.aareg.OpprettArbeidsforholdCommand;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.ArbeidsforholdResponseDTO;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class AaregConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public AaregConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient) {
        this.serverProperties = consumers.getTestnavDollyProxy();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Flux<Arbeidsforhold> hentArbeidsforhold(String ident) {

        return Flux.from(tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new HentArbeidsforholdCommand(webClient, token.getTokenValue(), ident).call()));
    }

    public Flux<ArbeidsforholdResponseDTO> opprettArbeidsforhold(Arbeidsforhold requests) {

        return Flux.from(tokenExchange.exchange(serverProperties)
                .flatMap(token -> new OpprettArbeidsforholdCommand(webClient,
                        requests, token.getTokenValue()).call()));
    }
}