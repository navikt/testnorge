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

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdoversikterDTO;

@Slf4j
@RequiredArgsConstructor
public class GetArbeidsforholdoversikterCommand implements Callable<ArbeidsforholdoversikterDTO> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String token;
    private final String orgnummer;

    @SneakyThrows
    @Override
    public ArbeidsforholdoversikterDTO call() {
        RequestEntity<Void> request = RequestEntity
                .get(new URI(url + "/v1/arbeidsgiver/arbeidsforholdoversikt"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(AaregHeaders.NAV_CONSUMER_TOKEN, "Bearer " + token)
                .header(AaregHeaders.NAV_ARBEIDSGIVERIDENT, orgnummer)
                .build();

        ResponseEntity<ArbeidsforholdoversikterDTO> response = restTemplate.exchange(request, ArbeidsforholdoversikterDTO.class);
        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody() || response.getBody() == null) {
            throw new RuntimeException(
                    "Klarer ikke å hente arbeidsforholdoversikt for " + orgnummer + ". Response code: " + response.getStatusCodeValue()
            );
        }
        return response.getBody();
    }
}
