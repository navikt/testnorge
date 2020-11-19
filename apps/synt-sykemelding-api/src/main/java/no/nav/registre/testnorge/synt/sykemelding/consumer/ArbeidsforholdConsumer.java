package no.nav.registre.testnorge.synt.sykemelding.consumer;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.synt.sykemelding.consumer.command.GetArbeidsforholdCommand;

@Component
@DependencyOn("testnorge-arbeidsforhold-api")
public class ArbeidsforholdConsumer {
    private final RestTemplate restTemplate;
    private final String url;

    public ArbeidsforholdConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.arbeidsforhold.url}") String url
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
    }

    @SneakyThrows
    public ArbeidsforholdDTO getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {
        return new GetArbeidsforholdCommand(restTemplate, url, ident, orgnummer, arbeidsforholdId).call();
    }
}
