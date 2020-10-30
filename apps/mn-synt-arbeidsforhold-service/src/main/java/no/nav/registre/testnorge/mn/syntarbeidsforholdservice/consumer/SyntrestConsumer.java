package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenererArbeidsforholdCommand;
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
            ArbeidsforholdDTO response = new GenererArbeidsforholdCommand(webClient, dto).call();
            return new Arbeidsforhold(response, arbeidsforhold.getIdent(), arbeidsforhold.getArbeidsforholdId());
        } catch (HttpServerErrorException.InternalServerError e) {
            log.error(objectMapper.writeValueAsString(dto), e);
            throw e;
        }
    }
}
