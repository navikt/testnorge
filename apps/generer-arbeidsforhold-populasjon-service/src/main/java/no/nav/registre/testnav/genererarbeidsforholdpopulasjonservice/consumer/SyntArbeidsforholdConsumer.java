package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GenererArbeidsforholdHistorikkCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GenererStartArbeidsforholdCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.credentials.SyntrestServiceProperties;
import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
public class SyntArbeidsforholdConsumer {
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public SyntArbeidsforholdConsumer(
            TokenExchange tokenExchange,
            SyntrestServiceProperties properties,
            ObjectMapper objectMapper
    ) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .build();
    }

    public Mono<List<ArbeidsforholdResponse>> genererStartArbeidsforhold(LocalDate startdato) {
        return tokenExchange
                .generateToken(properties)
                .flatMap(accessToken -> new GenererStartArbeidsforholdCommand(webClient, startdato, accessToken.getTokenValue()).call());
    }

    public Mono<List<List<ArbeidsforholdResponse>>> genererArbeidsforholdHistorikk(List<ArbeidsforholdRequest> requests) {
        return tokenExchange
                .generateToken(properties)
                .flatMap(accessToken -> new GenererArbeidsforholdHistorikkCommand(webClient, requests, accessToken.getTokenValue(), objectMapper).call());
    }
}
