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

import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.adapter.ArbeidsforholdHistorikkAdapter;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.SyntetiseringProperties;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateNextArbeidsforholdCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateNextArbeidsforholdWithHistorikkCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateStartArbeidsforholdCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;

@Slf4j
@Component
public class SyntrestConsumer {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ArbeidsforholdHistorikkAdapter adapter;
    private final SyntetiseringProperties properties;


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
    public ArbeidsforholdResponse getNesteArbeidsforholdResponse(Arbeidsforhold arbeidsforhold, LocalDate kaldermaaned) {
        var dto = arbeidsforhold.toSyntrestDTO(kaldermaaned);
        try {
            return new GenerateNextArbeidsforholdCommand(webClient, dto).call();
        } catch (WebClientResponseException.InternalServerError e) {
            throw new RuntimeException("Feil med opprettelse av arbeidsforhold: " + objectMapper.writeValueAsString(dto), e);
        }
    }

    @SneakyThrows
    public ArbeidsforholdResponse getNesteArbeidsforholdWithHistorikkResponse(Arbeidsforhold arbeidsforhold, LocalDate kaldermaaned) {
        String historikk = adapter.get(arbeidsforhold.getArbeidsforholdId()).getHistorikk();
        var dto = arbeidsforhold.toSyntrestDTO(kaldermaaned, historikk);
        try {
            ArbeidsforholdResponse response = new GenerateNextArbeidsforholdWithHistorikkCommand(webClient, dto).call();
            adapter.save(arbeidsforhold.getArbeidsforholdId(), response.getHistorikk());
            return response;
        } catch (WebClientResponseException.InternalServerError e) {
            throw new RuntimeException("Feil med opprettelse av arbeidsforhold: " + objectMapper.writeValueAsString(dto), e);
        }
    }

    @SneakyThrows
    public Arbeidsforhold getNesteArbeidsforhold(Arbeidsforhold arbeidsforhold, LocalDate kaldermaaned) {
        log.info("Finner neste arbeidsforhold den {}.", kaldermaaned.plusMonths(1));
        ArbeidsforholdResponse response = properties.isSaveHistory()
                ? getNesteArbeidsforholdWithHistorikkResponse(arbeidsforhold, kaldermaaned)
                : getNesteArbeidsforholdResponse(arbeidsforhold, kaldermaaned);
        return new Arbeidsforhold(
                response,
                arbeidsforhold.getIdent(),
                arbeidsforhold.getArbeidsforholdId(),
                arbeidsforhold.getVirksomhentsnummer()
        );
    }

    public Arbeidsforhold getFirstArbeidsforhold(LocalDate startdato, String ident, String virksomhetsnummer) {
        try {
            log.info("Oppretter nytt arbeidsforhold den {}.", startdato);
            ArbeidsforholdResponse response = new GenerateStartArbeidsforholdCommand(webClient, startdato).call();
            return new Arbeidsforhold(response, ident, virksomhetsnummer);
        } catch (WebClientResponseException.InternalServerError e) {
            throw new RuntimeException("Feil med start av arbeidsforhold for dato: " + startdato, e);
        }
    }
}