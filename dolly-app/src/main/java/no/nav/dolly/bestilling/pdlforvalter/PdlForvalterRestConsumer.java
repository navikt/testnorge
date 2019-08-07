package no.nav.dolly.bestilling.pdlforvalter;

import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class PdlForvalterRestConsumer {

    private static final String PDL_BESTILLING_URL = "/api/v1/bestilling";
    private static final String PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL = PDL_BESTILLING_URL + "/kontaktinformasjonfordoedsbo";
    private static final String PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL = PDL_BESTILLING_URL + "/utenlandsidentifikasjonsnummer";
    private static final String PDL_BESTILLING_FALSK_IDENTITET_URL = PDL_BESTILLING_URL + "/falskidentitet";
    private static final String PDL_BESTILLING_SLETTING_URL = "/api/v1/ident";
    private static final String PREPROD_ENV = "q";

    @Value("${fasit.environment.name}")
    private String environment;

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    public ResponseEntity deleteIdent(String ident) {
        return doRequest(URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_SLETTING_URL),
                HttpMethod.DELETE, null, ident);
    }

    public ResponseEntity postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo, String ident) {
        return doRequest(URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL),
                HttpMethod.POST, kontaktinformasjonForDoedsbo, ident);
    }

    public ResponseEntity postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer utenlandskIdentifikasjonsnummer, String ident) {
        return doRequest(URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL),
                HttpMethod.POST, utenlandskIdentifikasjonsnummer, ident);
    }

    public ResponseEntity postFalskIdentitet(PdlFalskIdentitet falskIdentitet, String ident) {
        return doRequest(URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FALSK_IDENTITET_URL),
                HttpMethod.POST, falskIdentitet, ident);
    }

    private ResponseEntity doRequest(URI uri, HttpMethod method, Object body, String ident) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        httpHeaders.add(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV));
        httpHeaders.add(HEADER_NAV_CONSUMER_TOKEN, resolveToken());
        httpHeaders.add(HEADER_NAV_PERSON_IDENT, ident);

        return restTemplate.exchange(uri, method, new HttpEntity<>(body, httpHeaders), JsonNode.class);
    }

    private String resolveToken() {
        return environment.toLowerCase().contains(PREPROD_ENV) ? StsOidcService.getUserIdToken() : stsOidcService.getIdToken(PREPROD_ENV);
    }
}
