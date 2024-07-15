package no.nav.registre.testnorge.levendearbeidsforhold.consumers;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforhold.consumers.command.EndreArbeidsforholdCommand;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.command.HentArbeidsforholdCommand;
import no.nav.registre.testnorge.levendearbeidsforhold.domain.v1.Arbeidsforhold;
import no.nav.registre.testnorge.levendearbeidsforhold.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;

import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class AaregConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public AaregConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper) {

        this.serverProperties = consumers.getTestnavAaregProxy();
        this.tokenExchange = tokenExchange;

        ExchangeStrategies jacksonStrategy = ExchangeStrategies
            .builder()
            .codecs(
                config -> {
                    config
                        .defaultCodecs()
                        .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config
                        .defaultCodecs()
                        .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
            .build();

        this.webClient = WebClient
            .builder()
            .exchangeStrategies(jacksonStrategy)
            .baseUrl(serverProperties.getUrl())
            .build();
    }

    public List<Arbeidsforhold> hentArbeidsforhold(String ident) {
        var token = tokenExchange.exchange(serverProperties).block();
        if (nonNull(token)) {
            return new HentArbeidsforholdCommand(webClient, token.getTokenValue(), ident).call();
        }
        return new ArrayList<>();
    }

    public void endreArbeidsforhold(Arbeidsforhold requests) {
        var token = tokenExchange.exchange(serverProperties).block();
        if (nonNull(token)) {
            new EndreArbeidsforholdCommand(webClient, requests, token.getTokenValue()).call();
        }
    }
}
