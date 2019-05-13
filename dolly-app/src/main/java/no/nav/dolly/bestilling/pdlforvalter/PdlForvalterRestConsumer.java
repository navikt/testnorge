package no.nav.dolly.bestilling.pdlforvalter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.folkeregister.PdlFolkeregisterIdent;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.sts.StsOidcService;

@Service
public class PdlForvalterRestConsumer {

    private static final String PDL_BESTILLING_FOLKEREGISTER_IDENT_URL = "/api/v1/bestilling/folkeregisterident";
    private static final String PDL_BESTILLING_KONTAKTINFORMASJON_FOR_DODESDBO_URL = "/api/v1/bestilling/kontaktinformasjonfordoedsbo";
    private static final String PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER = "/api/v1/bestilling/utenlandsidentifikasjonsnummer";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvidersProps providersProps;

    @Autowired
    private StsOidcService stsOidcService;

    public ResponseEntity postFolkeregisterIdent(PdlFolkeregisterIdent folkeregisterIdent) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FOLKEREGISTER_IDENT_URL))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken("q"))
                .body(folkeregisterIdent), String.class);
    }

    public ResponseEntity postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_KONTAKTINFORMASJON_FOR_DODESDBO_URL))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken("q"))
                .body(kontaktinformasjonForDoedsbo), String.class);
    }

    public ResponseEntity postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer utenlandskIdentifikasjonsnummer) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken("q"))
                .body(utenlandskIdentifikasjonsnummer), String.class);
    }
}
