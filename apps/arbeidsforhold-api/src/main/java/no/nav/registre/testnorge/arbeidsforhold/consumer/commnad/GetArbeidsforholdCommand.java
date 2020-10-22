package no.nav.registre.testnorge.arbeidsforhold.consumer.commnad;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

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
    private final String miljo;

    @SneakyThrows
    @Override
    public Arbeidsforhold call() {

        try {
            log.trace("Henter arbeidsforhold for navArbeidsforholdId {}...", navArbeidsforholdId);
            RequestEntity<Void> request = RequestEntity
                    .get(new UriTemplate(url + "/v1/arbeidsforhold/{navArbeidsforholdId}").expand(miljo, navArbeidsforholdId))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(AaregHeaders.NAV_CONSUMER_TOKEN, "Bearer " + token)
                    .build();

            var response = restTemplate.exchange(request, ArbeidsforholdDTO.class);

            log.trace("Hentet arbeidsforhold for navArbeidsforholdId {}.", navArbeidsforholdId);
            return new Arbeidsforhold(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Fant ikke arbeidsforhold for navArbeidsforholdId {} i miljo {}", navArbeidsforholdId, miljo);
            return null;
        }
    }
}
