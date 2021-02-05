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

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;

@Slf4j
@RequiredArgsConstructor
public class CreateArbeidsforholdCommand implements Callable<Arbeidsforhold> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String token;
    private final Arbeidsforhold arbeidsforhold;

    @SneakyThrows
    @Override
    public Arbeidsforhold call() {

        RequestEntity<ArbeidsforholdDTO> request = RequestEntity
                .post(new UriTemplate(url + "/v1/arbeidsforhold").expand("q2"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(AaregHeaders.NAV_CONSUMER_TOKEN, "Bearer " + token)
                .header(AaregHeaders.NAV_ARBEIDSFORHOLD_KILDEREFERANSE, "testnorge")
                .body(new ArbeidsforholdDTO(arbeidsforhold));

        log.info("Oppretter arbeidsforhold for {}", arbeidsforhold.getIdent());

        ResponseEntity<ArbeidsforholdDTO> response = restTemplate.exchange(request, ArbeidsforholdDTO.class);
        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
            log.error(
                    "Klarer ikke å opprette arbeidsforhold for {}. Response code: {}",
                    arbeidsforhold.getIdent(),
                    response.getStatusCodeValue()
            );
            return null;
        }
        return new Arbeidsforhold(response.getBody());
    }
}
