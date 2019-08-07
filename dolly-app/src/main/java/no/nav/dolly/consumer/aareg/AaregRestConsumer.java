package no.nav.dolly.consumer.aareg;

import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.security.sts.StsOidcService;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class AaregRestConsumer {

    private final AaregArbeidsforholdFasitConsumer aaregArbeidsforholdFasitConsumer;
    private final RestTemplate restTemplate;
    private final StsOidcService stsOidcService;

    public ResponseEntity<Object[]> readArbeidsforhold(String ident, String environment) {

        return restTemplate.exchange(RequestEntity
                        .get(URI.create(aaregArbeidsforholdFasitConsumer.getUrlForEnv(environment)))
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, stsOidcService.getIdToken(environment))
                        .header(HEADER_NAV_CONSUMER_TOKEN, stsOidcService.getIdToken(environment))
                        .header(HEADER_NAV_PERSON_IDENT, ident)
                        .build(),
                Object[].class);
    }
}
