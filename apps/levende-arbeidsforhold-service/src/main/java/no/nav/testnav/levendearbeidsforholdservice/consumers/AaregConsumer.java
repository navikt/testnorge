package no.nav.testnav.levendearbeidsforholdservice.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdservice.config.Consumers;
import no.nav.testnav.levendearbeidsforholdservice.consumers.command.EndreArbeidsforholdCommand;
import no.nav.testnav.levendearbeidsforholdservice.consumers.command.HentArbeidsforholdCommand;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
public class AaregConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public AaregConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        this.serverProperties = consumers.getTestnavAaregProxy();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public List<Arbeidsforhold> hentArbeidsforhold(String ident) {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new HentArbeidsforholdCommand(webClient, token.getTokenValue(), ident).call())
                .collectList()
                .block();
    }

    public void endreArbeidsforhold(Arbeidsforhold requests) {

        tokenExchange.exchange(serverProperties)
                .flatMap(token -> new EndreArbeidsforholdCommand(webClient, requests, token.getTokenValue()).call())
                .doOnNext(status -> log.info("Status fra endre arbeidsforhold:", status))
                .block();
    }
}