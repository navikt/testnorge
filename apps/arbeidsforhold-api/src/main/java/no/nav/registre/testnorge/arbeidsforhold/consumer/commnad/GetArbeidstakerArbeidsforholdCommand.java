package no.nav.registre.testnorge.arbeidsforhold.consumer.commnad;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;

@Slf4j
@RequiredArgsConstructor
public class GetArbeidstakerArbeidsforholdCommand implements Callable<List<Arbeidsforhold>> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String token;
    private final String ident;

    @SneakyThrows
    @Override
    public List<Arbeidsforhold> call() {
        RequestEntity<Void> request = RequestEntity
                .get(new UriTemplate(url + "/v1/arbeidstaker/arbeidsforhold").expand("q2"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(AaregHeaders.NAV_CONSUMER_TOKEN, "Bearer " + token)
                .header(AaregHeaders.NAV_PERSON_IDENT, ident)
                .build();

        ResponseEntity<ArbeidsforholdDTO[]> response = restTemplate.exchange(request, ArbeidsforholdDTO[].class);
        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody() || response.getBody()==null) {
            log.error(
                    "Klarer ikke å hente arbeidsforhold for {}. Response code: {}",
                    ident,
                    response.getStatusCodeValue()
            );
            return Collections.emptyList();
        }
        return Arrays.stream(response.getBody()).map(Arbeidsforhold::new).collect(Collectors.toList());
    }
}
