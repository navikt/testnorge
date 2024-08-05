package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.aareg.HentArbeidsforholdCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.aareg.OpprettArbeidsforholdCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

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
        var accessToken = tokenExchange.exchange(serverProperties).block();
        if (accessToken != null) {
            return new HentArbeidsforholdCommand(webClient, accessToken.getTokenValue(), ident).call();
        } else {
            return new ArrayList<>();
        }
    }

    public Optional<HttpStatusCode> opprettArbeidsforhold(Arbeidsforhold requests) {
        var accessToken = tokenExchange.exchange(serverProperties).block();
        if (accessToken != null){
            return Optional.of(new OpprettArbeidsforholdCommand(webClient, requests, accessToken.getTokenValue())
                    .call().getStatusCode());
        } else {
            return Optional.empty();
        }
    }
}

