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

import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.adapter.ArbeidsforholdHistorikkAdapter;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.SyntetiseringProperties;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateArbeidsforholdHistorikkCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateNextArbeidsforholdCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateNextArbeidsforholdWithHistorikkCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateStartArbeidsforholdCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.exception.SyntetiseringException;

@Slf4j
@Component
public class SyntrestConsumer {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ArbeidsforholdHistorikkAdapter adapter;
    private final SyntetiseringProperties properties;

    private static final String OPPRETTELSE_FEILMELDING = "Feil med opprettelse av arbeidsforhold: ";

    public SyntrestConsumer(
            @Value("${consumers.syntrest.url}") String url,
            ObjectMapper objectMapper,
            ArbeidsforholdHistorikkAdapter adapter,
            SyntetiseringProperties properties
    ) {
        this.objectMapper = objectMapper;
        this.adapter = adapter;
        this.properties = properties;
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
    private ArbeidsforholdResponse getNesteArbeidsforholdResponse(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned) {
        var dto = arbeidsforhold.toSyntrestDTO(kalendermaaned);
        try {
            return new GenerateNextArbeidsforholdCommand(webClient, dto).call();
        } catch (WebClientResponseException.InternalServerError e) {
            throw new SyntetiseringException(OPPRETTELSE_FEILMELDING + objectMapper.writeValueAsString(dto), e);
        }
    }

    @SneakyThrows
    private List<ArbeidsforholdResponse> getArbeidsforholdHistorikkResponse(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned) {
        var dto = arbeidsforhold.toSyntrestDTO(kalendermaaned);
        try {
            return new GenerateArbeidsforholdHistorikkCommand(webClient, dto).call();
        } catch (WebClientResponseException.InternalServerError e) {
            throw new SyntetiseringException(OPPRETTELSE_FEILMELDING + objectMapper.writeValueAsString(dto), e);
        }
    }

    @SneakyThrows
    private ArbeidsforholdResponse getNesteArbeidsforholdWithHistorikkResponse(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned) {
        String historikk = adapter.get(arbeidsforhold.getArbeidsforholdId()).getHistorikk();
        var dto = arbeidsforhold.toSyntrestDTO(kalendermaaned, historikk);
        try {
            ArbeidsforholdResponse response = new GenerateNextArbeidsforholdWithHistorikkCommand(webClient, dto).call();
            adapter.save(arbeidsforhold.getArbeidsforholdId(), response.getHistorikk());
            return response;
        } catch (WebClientResponseException.InternalServerError e) {
            throw new SyntetiseringException(OPPRETTELSE_FEILMELDING + objectMapper.writeValueAsString(dto), e);
        }
    }

    @SneakyThrows
    public Arbeidsforhold getNesteArbeidsforhold(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned) {
        log.info("Finner neste arbeidsforhold den {}.", kalendermaaned.plusMonths(1));
        ArbeidsforholdResponse response = properties.isSaveHistory()
                ? getNesteArbeidsforholdWithHistorikkResponse(arbeidsforhold, kalendermaaned)
                : getNesteArbeidsforholdResponse(arbeidsforhold, kalendermaaned);
        return new Arbeidsforhold(
                response,
                arbeidsforhold.getIdent(),
                arbeidsforhold.getArbeidsforholdId(),
                arbeidsforhold.getVirksomhetsnummer()
        );
    }

    @SneakyThrows
    public List<Arbeidsforhold> getArbeidsforholdHistorikk(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned) {
        log.info("Finner arbeidsforhold historikk fra og med {}.", kalendermaaned.plusMonths(1));
        List<ArbeidsforholdResponse> response = getArbeidsforholdHistorikkResponse(arbeidsforhold, kalendermaaned);
        log.info("Fant historikk for {} måneder.", response.size());

        var list = response.stream().map(res -> new Arbeidsforhold(
                res,
                arbeidsforhold.getIdent(),
                arbeidsforhold.getArbeidsforholdId(),
                arbeidsforhold.getVirksomhetsnummer()))
                .collect(Collectors.toList());

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
            return new Arbeidsforhold(response, ident, virksomhetsnummer);
        } catch (WebClientResponseException.InternalServerError e) {
            throw new SyntetiseringException("Feil med start av arbeidsforhold for dato: " + startdato, e);
        }
    }
}