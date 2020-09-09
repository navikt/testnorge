package no.nav.registre.aareg.consumer.rs;

import static no.nav.registre.aareg.util.ArbeidsforholdMappingUtil.mapToArbeidsforhold;
import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;
import static no.nav.registre.aareg.domain.CommonKeys.RESPONSE_TYPE_LIST_ARBEIDSFORHOLD;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.domain.dto.aordningen.arbeidsforhold.Arbeidsforhold;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.aareg.security.sts.StsOidcService;

@Slf4j
@Component
@RequiredArgsConstructor
@DependencyOn(value = "aareg", external = true)
public class AaregRestConsumer {

    private final AaregArbeidsforholdFasitConsumer aaregArbeidsforholdFasitConsumer;
    private final RestTemplate restTemplate;
    private final StsOidcService stsOidcService;

    public ResponseEntity<List<Arbeidsforhold>> hentArbeidsforhold(
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
        ResponseEntity<List<JsonNode>> response;
        List<Arbeidsforhold> arbeidsforhold = new ArrayList<>();
        try {
            response = restTemplate.exchange(getRequest, RESPONSE_TYPE_LIST_ARBEIDSFORHOLD);
            if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                for (var arbeidsforholdet : response.getBody()) {
                    arbeidsforhold.add(mapToArbeidsforhold(arbeidsforholdet));
                }
            }
        } catch (ResourceAccessException e) {
            log.warn("Kunne ikke hente ident fra miljø", e);
        } catch (HttpStatusCodeException e) {
            log.info("Kunne ikke hente ident fra aareg", e);
            return ResponseEntity.status(e.getStatusCode()).build();
        }
        return ResponseEntity.ok().body(arbeidsforhold);
    }
}
