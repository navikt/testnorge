package no.nav.registre.testnorge.arbeidsforhold.consumer.commnad;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdoversikterDTO;

@Slf4j
@RequiredArgsConstructor
public class GetArbeidsforholdoversiktCommand implements Callable<ArbeidsforholdoversikterDTO> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String token;
    private final String orgnummer;
    private final String miljo;

    @SneakyThrows
    @Override
    public ArbeidsforholdoversikterDTO call() {
        try {
            RequestEntity<Void> request = RequestEntity
                    .get(new UriTemplate(url + "/v1/arbeidsgiver/arbeidsforholdoversikt").expand(miljo))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(AaregHeaders.NAV_CONSUMER_TOKEN, "Bearer " + token)
                    .header(AaregHeaders.NAV_ARBEIDSGIVERIDENT, orgnummer)
                    .build();

            var response = restTemplate.exchange(request, ArbeidsforholdoversikterDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Fant ikke arbeidsforholdoversikt for organisajon {} i miljo {}", orgnummer, miljo);
            return null;
        }

    }
}
