package no.nav.registre.testnorge.arbeidsforhold.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;
import no.nav.registre.testnorge.arbeidsforhold.service.StsOidcTokenService;

@Slf4j
@Component
public class AaregConsumer {

    private static final String HEADER_NAV_CONSUMER_TOKEN = "Nav-Consumer-Token";
    private static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";

    private final StsOidcTokenService tokenService;
    private final RestTemplate restTemplate;
    private final String url;

    public AaregConsumer(StsOidcTokenService tokenService, RestTemplateBuilder restTemplateBuilder, @Value("${aareg.url}") String url) {
        this.tokenService = tokenService;
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
    }

    @SneakyThrows
    public List<Arbeidsforhold> getArbeidsforholds(String ident) {
        RequestEntity<Void> request = RequestEntity
                .get(new URI(url + "/v1/arbeidstaker/arbeidsforhold"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getToken())
                .header(HEADER_NAV_CONSUMER_TOKEN, "Bearer " + tokenService.getToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .build();
        ResponseEntity<ArbeidsforholdDTO[]> response = restTemplate.exchange(request, ArbeidsforholdDTO[].class);
        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
            log.warn(
                    "Klarer ikke å hente arbeidsforhold for {}. Response code: {}",
                    ident,
                    response.getStatusCodeValue()
            );
        }
        return Arrays.stream(response.getBody()).map(Arbeidsforhold::new).collect(Collectors.toList());
    }

    public List<Arbeidsforhold> getArbeidsforholds(String ident, String orgnummer) {
        return getArbeidsforholds(ident)
                .stream()
                .filter(value -> value.getOrgnummer().equals(orgnummer))
                .collect(Collectors.toList());
    }

    public Arbeidsforhold getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {
        return getArbeidsforholds(ident, orgnummer)
                .stream()
                .filter(value -> value.getArbeidsforholdId().equals(arbeidsforholdId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Klarer ikke aa finne arbeidsforhold for " + ident));
    }
}
