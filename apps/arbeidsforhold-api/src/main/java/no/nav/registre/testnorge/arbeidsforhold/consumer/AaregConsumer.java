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
import java.util.Optional;

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;
import no.nav.registre.testnorge.arbeidsforhold.service.StsOidcTokenService;

@Slf4j
@Component
public class AaregConsumer {

    private final StsOidcTokenService tokenService;
    private final RestTemplate restTemplate;
    private final String url;

    public AaregConsumer(StsOidcTokenService tokenService, RestTemplateBuilder restTemplateBuilder, @Value("${aareg.url}") String url) {
        this.tokenService = tokenService;
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
    }

    @SneakyThrows
    public Arbeidsforhold getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {
        RequestEntity<Void> request = RequestEntity
                .get(new URI(url + "/v1/arbeidstaker/arbeidsforhold"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getToken())
                .build();
        ResponseEntity<ArbeidsforholdDTO[]> response = restTemplate.exchange(request, ArbeidsforholdDTO[].class);

        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
            log.warn(
                    "Klarer ikke aa hente arbeidsforhold for {} med orgnummer {} og arbeidsforholdId {}. Response code: {}",
                    ident,
                    orgnummer,
                    arbeidsforholdId,
                    response.getStatusCodeValue()
            );
        }

        Optional<ArbeidsforholdDTO> arbeidsforhold = Arrays.stream(response.getBody())
                .filter(value -> value.getArbeidsgiver().getOrganisasjonsnummer().equals(orgnummer))
                .filter(value -> value.getArbeidsforholdId().equals(arbeidsforholdId))
                .findFirst();

        if (arbeidsforhold.isEmpty()) {
            throw new RuntimeException("KLarer ikke å finne arbeidsforhold for" + ident);
        }
        return new Arbeidsforhold(arbeidsforhold.get());
    }

}
