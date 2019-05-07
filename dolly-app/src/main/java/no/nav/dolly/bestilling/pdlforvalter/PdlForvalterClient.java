package no.nav.dolly.bestilling.pdlforvalter;

import static java.lang.String.format;

import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.folkeregister.PdlFolkeregisterIdent;
import no.nav.dolly.properties.ProvidersProps;

@Service
public class PdlForvalterClient {

    private static final String PDL_BESTILLING_FOLKEREGISTER_IDENT_URL = "/api/v1/bestilling/folkeregisterident";
    private static final String PDL_BESTILLING_KONTAKTINFORMASJON_FOR_DODESDBO_URL = "/api/v1/bestilling/kontaktinformasjonfordoedsbo";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvidersProps providersProps;

    public ResponseEntity postFolkeregisterIdent(PdlFolkeregisterIdent folkeregisterIdent) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(format("%s%?ident=%s", providersProps.getPdlForvalter().getUrl(),
                        PDL_BESTILLING_FOLKEREGISTER_IDENT_URL, folkeregisterIdent.getIdnummer())))
                .body(folkeregisterIdent), JsonNode.class);
    }

    public ResponseEntity postKontaktinformasjonForDoedsbo(String ident, PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(format("%s%s?ident=%s", providersProps.getPdlForvalter().getUrl(),
                        PDL_BESTILLING_KONTAKTINFORMASJON_FOR_DODESDBO_URL, ident)))
                .body(kontaktinformasjonForDoedsbo), JsonNode.class);
    }

}
