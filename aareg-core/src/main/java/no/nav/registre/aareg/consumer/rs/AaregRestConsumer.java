package no.nav.registre.aareg.consumer.rs;

import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;
import static no.nav.registre.aareg.domain.CommonKeys.RESPONSE_TYPE_LIST_MAP;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

import no.nav.registre.aareg.security.sts.StsOidcService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AaregRestConsumer {

    private final AaregArbeidsforholdFasitConsumer aaregArbeidsforholdFasitConsumer;
    private final RestTemplate restTemplate;
    private final StsOidcService stsOidcService;

    public ResponseEntity<List<Map>> hentArbeidsforhold(
            String ident,
            String miljoe
    ) {
        var getRequest = RequestEntity
                .get(URI.create(aaregArbeidsforholdFasitConsumer.getUrlForEnv(miljoe)))
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, stsOidcService.getIdToken(miljoe))
                .header(HEADER_NAV_CONSUMER_TOKEN, stsOidcService.getIdToken(miljoe))
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .build();
        ResponseEntity<List<Map>> response = null;
        try {
            response = restTemplate.exchange(getRequest, RESPONSE_TYPE_LIST_MAP);
        } catch (ResourceAccessException e) {
            log.warn("Kunne ikke hente ident {} i miljø {}", ident, miljoe, e);
        }
        return response;
    }
}
