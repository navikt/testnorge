package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.aareg.HentArbeidsforholdCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.aareg.OpprettArbeidsforholdCommand;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class AaregConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public AaregConsumer(
            Consumers consumers,
            TokenExchange tokenExchange) {

        this.serverProperties = consumers.getTestnavAaregProxy();
        this.tokenExchange = tokenExchange;

        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public List<Arbeidsforhold> hentArbeidsforhold(String ident) {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new HentArbeidsforholdCommand(webClient, token.getTokenValue(), ident).call())
                .collectList()
                .block();
    }

    public Optional<HttpStatusCode> opprettArbeidsforhold(Arbeidsforhold requests) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new OpprettArbeidsforholdCommand(webClient, requests, token.getTokenValue()).call())
                .map(resultat -> Optional.of(resultat)
                        .map(ResponseEntity::getStatusCode))
                .block();
    }
}

