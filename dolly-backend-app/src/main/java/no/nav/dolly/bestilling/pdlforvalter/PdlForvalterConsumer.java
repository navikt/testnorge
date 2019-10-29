package no.nav.dolly.bestilling.pdlforvalter;

import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@Service
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

    @Value("${fasit.environment.name}")
    private String environment;

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    public ResponseEntity deleteIdent(String ident) {
        return restTemplate.exchange(RequestEntity.delete(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_SLETTING_URL))
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, resolveToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .build(), JsonNode.class);
    }

    public ResponseEntity postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo, String ident) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, resolveToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .body(kontaktinformasjonForDoedsbo), JsonNode.class);
    }

    public ResponseEntity postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer utenlandskIdentifikasjonsnummer, String ident) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, resolveToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .body(utenlandskIdentifikasjonsnummer), JsonNode.class);
    }

    public ResponseEntity postFalskIdentitet(PdlFalskIdentitet falskIdentitet, String ident) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FALSK_IDENTITET_URL))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, resolveToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .body(falskIdentitet), JsonNode.class);
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

        return environment.toLowerCase().contains(PREPROD_ENV) ?  StsOidcService.getUserIdToken() : stsOidcService.getIdToken(PREPROD_ENV);
    }
}
