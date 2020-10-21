package no.nav.registre.testnorge.arbeidsforhold.consumer.commnad;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;

@Slf4j
@RequiredArgsConstructor
public class GetArbeidsforholdCommand implements Callable<Arbeidsforhold> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String token;
    private final Integer navArbeidsforholdId;

    @SneakyThrows
    @Override
    public Arbeidsforhold call() {
        log.info("Henter arbeidsforhold for navArbeidsforholdId {}...", navArbeidsforholdId);
        RequestEntity<Void> request = RequestEntity
                .get(new URI(url + "/v1/arbeidstaker/" + navArbeidsforholdId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(AaregHeaders.NAV_CONSUMER_TOKEN, "Bearer " + token)
                .build();

        ResponseEntity<ArbeidsforholdDTO> response = restTemplate.exchange(request, ArbeidsforholdDTO.class);
        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody() || response.getBody() == null) {
            throw new RuntimeException(
                    "Klarer ikke å hente arbeidsforhold for " + navArbeidsforholdId + ". Response code: " + response.getStatusCodeValue()
            );
        }

        log.info("Hentet arbeidsforhold for navArbeidsforholdId {}.", navArbeidsforholdId);
        return new Arbeidsforhold(response.getBody());
    }
}
