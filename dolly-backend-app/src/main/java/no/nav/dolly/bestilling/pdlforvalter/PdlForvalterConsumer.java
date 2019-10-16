package no.nav.dolly.bestilling.pdlforvalter;

import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;
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
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class PdlForvalterConsumer {

    private static final String PDL_BESTILLING_URL = "/api/v1/bestilling";
    private static final String PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL = PDL_BESTILLING_URL + "/kontaktinformasjonfordoedsbo";
    private static final String PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL = PDL_BESTILLING_URL + "/utenlandsidentifikasjonsnummer";
    private static final String PDL_BESTILLING_FALSK_IDENTITET_URL = PDL_BESTILLING_URL + "/falskidentitet";
    private static final String PDL_BESTILLING_FOEDSEL_URL = PDL_BESTILLING_URL + "/foedsel";
    private static final String PDL_BESTILLING_DOEDSFALL_URL = PDL_BESTILLING_URL + "/doedsfall";
    private static final String PDL_BESTILLING_ADRESSEBESKYTTELSE_URL = PDL_BESTILLING_URL + "/adressebeskyttelse";
    private static final String PDL_BESTILLING_SLETTING_URL = "/api/v1/ident";
    private static final String PREPROD_ENV = "q";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    @Value("${fasit.environment.name}")
    private String environment;


    public ResponseEntity deleteIdent(String ident) {
        return doRequest(DELETE, createFullURI(PDL_BESTILLING_SLETTING_URL), null, ident);
    }

    public ResponseEntity postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo body, String ident) {
        return doRequest(POST, createFullURI(PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL), body, ident);
    }

    public ResponseEntity postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer body, String ident) {
        return doRequest(POST, createFullURI(PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL), body, ident);
    }

    public ResponseEntity postFalskIdentitet(PdlFalskIdentitet body, String ident) {
        return doRequest(POST, createFullURI(PDL_BESTILLING_FALSK_IDENTITET_URL), body, ident);
    }

    private URI createFullURI(String url) {
        return URI.create(providersProps.getPdlForvalter().getUrl() + url);
    }

    private ResponseEntity doRequest(HttpMethod method, URI uri, Object body, String ident) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        httpHeaders.add(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV));
        httpHeaders.add(HEADER_NAV_CONSUMER_TOKEN, resolveToken());
        httpHeaders.add(HEADER_NAV_PERSON_IDENT, ident);

        return restTemplate.exchange(uri, method, new HttpEntity<>(body, httpHeaders), JsonNode.class);
    }

    public ResponseEntity postFoedsel(PdlFoedsel pdlFoedsel, String ident) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FOEDSEL_URL))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, resolveToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .body(pdlFoedsel), JsonNode.class);
    }

    public ResponseEntity postDoedsfall(PdlDoedsfall pdlDoedsfall, String ident) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_DOEDSFALL_URL))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, resolveToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .body(pdlDoedsfall), JsonNode.class);
    }

    public ResponseEntity postAdressebeskyttelse(PdlAdressebeskyttelse pdlAdressebeskyttelse, String ident) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_ADRESSEBESKYTTELSE_URL))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, resolveToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .body(pdlAdressebeskyttelse), JsonNode.class);
    }

    private String resolveToken() {
        return environment.toLowerCase().contains(PREPROD_ENV) ? StsOidcService.getUserIdToken() : stsOidcService.getIdToken(PREPROD_ENV);
    }
}