package no.nav.registre.testnorge.synt.arbeidsforhold.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.SyntArbeidsforholdDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.SyntDTO;

@Slf4j
@Component
public class SyntArbeidsforholdConsumer {
    private final RestTemplate restTemplate;
    private final String url;


    public SyntArbeidsforholdConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.synt-arbeidsforhold.url}") String url) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url + "/api/v1/generate/aareg";
    }

    public SyntArbeidsforholdDTO genererArbeidsforhold(String ident) {
        log.info("Syntentiserer arbeidsforhold for {}.", ident);

        RequestEntity<List<String>> request = RequestEntity
                .post(URI.create(url))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Collections.singletonList(ident));

        ResponseEntity<SyntDTO[]> response = restTemplate.exchange(request, SyntDTO[].class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().length == 0) {
            throw new RuntimeException(
                    "Klarte ikke å generere arbeidsforhold for " + ident + ". (http status:" + response.getStatusCodeValue() + ")"
            );
        }
        if (response.getBody().length > 1) {
            log.warn("Uventet antall arbeidsforhold for {}. Returnerer det første i listen", ident);
        }
        return response.getBody()[0].getArbeidsforhold();
    }
}