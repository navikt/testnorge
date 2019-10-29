package no.nav.registre.aareg.consumer.rs;

import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

import no.nav.registre.aareg.security.sts.StsOidcService;

@Component
@RequiredArgsConstructor
public class AaregRestConsumer {

    private final AaregArbeidsforholdFasitConsumer aaregArbeidsforholdFasitConsumer;
    private final RestTemplate restTemplate;
    private final StsOidcService stsOidcService;

    public ResponseEntity<List<Map>> hentArbeidsforhold(
            String ident, String miljoe
    ) {
        RequestEntity getRequest = RequestEntity
                .get(URI.create(aaregArbeidsforholdFasitConsumer.getUrlForEnv(miljoe)))
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, stsOidcService.getIdToken(miljoe))
                .header(HEADER_NAV_CONSUMER_TOKEN, stsOidcService.getIdToken(miljoe))
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .build();
        return restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<Map>>() {
        });
    }
}
