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
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateNextArbeidsforholdCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenerateStartArbeidsforholdCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;

@Slf4j
@Component
public class SyntrestConsumer {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public SyntrestConsumer(@Value("${consumers.syntrest.url}") String url, ObjectMapper objectMapper) {
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
    public Arbeidsforhold getNesteArbeidsforhold(Arbeidsforhold arbeidsforhold, LocalDate kaldermaaned) {
        var dto = arbeidsforhold.toSyntrestDTO(kaldermaaned);
        try {
            ArbeidsforholdResponse response = new GenerateNextArbeidsforholdCommand(webClient, dto).call();
            return new Arbeidsforhold(response, arbeidsforhold.getIdent(), arbeidsforhold.getArbeidsforholdId(), arbeidsforhold.getVirksomhentsnummer());
        } catch (WebClientResponseException.InternalServerError e) {
            log.error("Feil med opprellese av: {}", objectMapper.writeValueAsString(dto), e);
            throw e;
        }
    }

    public Arbeidsforhold getFirstArbeidsforhold(LocalDate startdato, String ident, String virksomhetsnummer) {
        ArbeidsforholdResponse response = new GenerateStartArbeidsforholdCommand(webClient, startdato).call();
        return new Arbeidsforhold(response, ident, virksomhetsnummer);
    }
}
