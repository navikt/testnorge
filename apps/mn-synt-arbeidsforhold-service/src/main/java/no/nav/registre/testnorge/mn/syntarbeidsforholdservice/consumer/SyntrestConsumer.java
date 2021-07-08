package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateArbeidsforholdHistorikkCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateStartArbeidsforholdCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.exception.SyntetiseringException;

@Slf4j
@Component
public class SyntrestConsumer {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final String OPPRETTELSE_FEILMELDING = "Feil med opprettelse av arbeidsforhold: ";

    public SyntrestConsumer(
            @Value("${consumers.syntrest.url}") String url,
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.webClient = WebClient
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .baseUrl(url)
                .build();
    }

    @SneakyThrows
    private List<ArbeidsforholdResponse> getArbeidsforholdHistorikkResponse(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned, Integer count) {
        var dto = arbeidsforhold.toSyntrestDTO(kalendermaaned, count);
        try {
            return new GenerateArbeidsforholdHistorikkCommand(webClient, dto).call();
        } catch (WebClientResponseException.InternalServerError e) {
            throw new SyntetiseringException(OPPRETTELSE_FEILMELDING + objectMapper.writeValueAsString(dto), e);
        }
    }

    public List<Arbeidsforhold> getArbeidsforholdHistorikk(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned) {
        return getArbeidsforholdHistorikk(arbeidsforhold, kalendermaaned, null);
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
        ).collect(Collectors.toList());

        list.stream()
                .filter(value -> value.getSluttdato() != null)
                .filter(value -> value.getStartdato().isAfter(value.getSluttdato()))
                .forEach(value -> log.warn("Sluttdato er før start dato (ident={}).", value.getIdent()));
        return list;
    }

    public Arbeidsforhold getFirstArbeidsforhold(LocalDate startdato, String ident, String virksomhetsnummer) {
        try {
            log.info("Oppretter nytt arbeidsforhold den {}.", startdato);
            ArbeidsforholdResponse response = new GenerateStartArbeidsforholdCommand(webClient, startdato).call();
            log.trace("Nytt arbeidsforhold av type: {}.", response.getArbeidsforholdType());
            return new Arbeidsforhold(response, ident, virksomhetsnummer);
        } catch (WebClientResponseException.InternalServerError e) {
            throw new SyntetiseringException("Feil med start av arbeidsforhold for dato: " + startdato, e);
        }
    }
}