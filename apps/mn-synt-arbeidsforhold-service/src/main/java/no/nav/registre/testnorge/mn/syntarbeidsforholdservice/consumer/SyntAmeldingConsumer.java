package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.credentials.SyntAmeldingProperties;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateArbeidsforholdHistorikkCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateStartArbeidsforholdCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.exception.SyntetiseringException;
import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SyntAmeldingConsumer {

    private final WebClient webClient;
    private final ServerProperties properties;
    private final TokenExchange tokenExchange;
    private final ObjectMapper objectMapper;

    private static final String OPPRETTELSE_FEILMELDING = "Feil med opprettelse av arbeidsforhold: ";

    public SyntAmeldingConsumer(
            TokenExchange tokenExchange,
            SyntAmeldingProperties properties,
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .baseUrl(properties.getUrl())
                .build();
    }

    @SneakyThrows
    private List<ArbeidsforholdResponse> getArbeidsforholdHistorikkResponse(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned, Integer count) {
        var dto = arbeidsforhold.toSyntrestDTO(kalendermaaned, count);
        try {
            var accessToken = tokenExchange.exchange(properties).block();
            return new GenerateArbeidsforholdHistorikkCommand(webClient, dto, accessToken.getTokenValue()).call();
        } catch (WebClientResponseException.InternalServerError e) {
            throw new SyntetiseringException(OPPRETTELSE_FEILMELDING + objectMapper.writeValueAsString(dto), e);
        }
    }

    @SneakyThrows
    public List<Arbeidsforhold> getArbeidsforholdHistorikk(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned, Integer count) {
        log.info("Finner arbeidsforhold historikk fra og med {}.", kalendermaaned.plusMonths(1));
        List<ArbeidsforholdResponse> response = getArbeidsforholdHistorikkResponse(arbeidsforhold, kalendermaaned, count);

        if (response.isEmpty()) {
            log.info("Fant ikke historikk. Prøver på nytt.");
            return getArbeidsforholdHistorikk(arbeidsforhold, kalendermaaned, count);
        }

        log.info("Fant historikk for {} måneder.", response.size());

        var list = response.stream().map(res -> new Arbeidsforhold(
                res,
                arbeidsforhold.getIdent(),
                arbeidsforhold.getArbeidsforholdId(),
                arbeidsforhold.getVirksomhetsnummer())
        ).toList();

        list.stream()
                .filter(value -> value.getSluttdato() != null)
                .filter(value -> value.getStartdato().isAfter(value.getSluttdato()))
                .forEach(value -> log.warn("Sluttdato er før start dato (ident={}).", value.getIdent()));
        return list;
    }

    public Arbeidsforhold getFirstArbeidsforhold(LocalDate startdato, String ident, String virksomhetsnummer) {
        try {
            log.info("Oppretter nytt arbeidsforhold den {}.", startdato);
            var accessToken = tokenExchange.exchange(properties).block();
            ArbeidsforholdResponse response = new GenerateStartArbeidsforholdCommand(webClient, startdato, accessToken.getTokenValue()).call();
            log.trace("Nytt arbeidsforhold av type: {}.", response.getArbeidsforholdType());
            return new Arbeidsforhold(response, ident, virksomhetsnummer);
        } catch (WebClientResponseException.InternalServerError e) {
            throw new SyntetiseringException("Feil med start av arbeidsforhold for dato: " + startdato, e);
        }
    }

}
