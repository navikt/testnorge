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
import no.nav.dolly.domain.resultset.pdlforvalter.navn.PdlNavn;
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
    private static final String PDL_BESTILLING_NAVN_URL = PDL_BESTILLING_URL + "/navn";
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

    public ResponseEntity postNavn(PdlNavn pdlNavn, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_NAVN_URL,
                pdlNavn, ident);
    }

    public ResponseEntity postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL,
                kontaktinformasjonForDoedsbo, ident);
    }

    public ResponseEntity postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer utenlandskIdentifikasjonsnummer, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL,
                utenlandskIdentifikasjonsnummer, ident);
    }

    public ResponseEntity postFalskIdentitet(PdlFalskIdentitet falskIdentitet, String ident) {

        return postRequest(
               providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FALSK_IDENTITET_URL, falskIdentitet, ident);
    }

    public ResponseEntity postFoedsel(PdlFoedsel pdlFoedsel, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FOEDSEL_URL, pdlFoedsel, ident);
    }

    public ResponseEntity postDoedsfall(PdlDoedsfall pdlDoedsfall, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_DOEDSFALL_URL,
                pdlDoedsfall, ident);
    }

    public ResponseEntity postAdressebeskyttelse(PdlAdressebeskyttelse pdlAdressebeskyttelse, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_ADRESSEBESKYTTELSE_URL,
                pdlAdressebeskyttelse, ident);
    }

    private ResponseEntity postRequest(String url, Object body, String ident) {

        return restTemplate.exchange(RequestEntity.post(URI.create(url))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, resolveToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .body(body), JsonNode.class);
    }

    private String resolveToken() {

        return environment.toLowerCase().contains(PREPROD_ENV) ? StsOidcService.getUserIdToken() : stsOidcService.getIdToken(PREPROD_ENV);
    }
}
