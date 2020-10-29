package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GenererArbeidsforholdCommand;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;


@Component
public class SyntrestConsumer {
    private final WebClient webClient;

    public SyntrestConsumer(@Value("${consumers.syntrest.url}") String url) {
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public Arbeidsforhold getNesteArbeidsforhold(Arbeidsforhold arbeidsforhold, LocalDate kaldermaaned) {
        ArbeidsforholdDTO dto = new GenererArbeidsforholdCommand(webClient, arbeidsforhold.toSyntrestDTO(kaldermaaned)).call();
        return new Arbeidsforhold(dto, arbeidsforhold.getIdent(), arbeidsforhold.getArbeidsforholdId());
    }
}
