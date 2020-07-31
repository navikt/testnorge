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

import no.nav.registre.testnorge.dto.arbeidsforhold.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.domain.Arbeidsforhold;

@Slf4j
@Component
public class ArbeidsforholdConsumer {
    private final RestTemplate restTemplate;
    private final String url;

    public ArbeidsforholdConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.arbeidsforhold.url}") String url) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url + "/api/v1/arbeidsforhold";
    }

    public void createArbeidsforhold(Arbeidsforhold arbeidsforhold) {
        var dto = arbeidsforhold.toDTO();
        log.info("Oppretter arbeidsforhold fom {} og tom {} for {}", dto.getFom(), dto.getTom(), dto.getIdent());
        RequestEntity<ArbeidsforholdDTO> request = RequestEntity
                .post(URI.create(url))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);

        ResponseEntity<ArbeidsforholdDTO> response = restTemplate.exchange(request, ArbeidsforholdDTO.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Klarte ikke å opprette nytt arbeidsforhold. (https code: " + response.getStatusCodeValue() + ")");
        }
    }

}
