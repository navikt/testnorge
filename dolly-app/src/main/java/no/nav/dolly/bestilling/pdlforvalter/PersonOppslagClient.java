package no.nav.dolly.bestilling.pdlforvalter;

import static java.lang.String.format;
import static no.nav.dolly.sts.StsOidcService.getUserIdToken;

import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.HttpHeaders;

import no.nav.dolly.domain.resultset.pdlforvalter.TemaGrunnlag;
import no.nav.dolly.properties.ProvidersProps;

@Service
public class PersonOppslagClient {
    private static final String PDL_OPPLYSNING_URL = "/api/v1/opplysning/{opplysningsId}";
    private static final String PDL_OPPSLAG_URL = "/api/v1/oppslag";
    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String NAV_CONSUMER_TOKEN = "Nav-Consumer-Token";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvidersProps providersProps;

    public ResponseEntity getOpplysning(String ident) {
        return restTemplate.exchange(RequestEntity.delete(
                URI.create(format("%s%s?personident=%s", providersProps.getPersonOppslag().getUrl(), PDL_OPPLYSNING_URL, ident)))
                .header(NAV_CALL_ID, getCallId())
                .header(NAV_CONSUMER_ID, "Dolly")
                .build(), JsonNode.class);
    }

    public ResponseEntity getOppslag(TemaGrunnlag tema, String opplysningId ) {
        return restTemplate.exchange(RequestEntity.get(
                URI.create(providersProps.getPersonOppslag().getUrl() + PDL_OPPSLAG_URL))
                .header(HttpHeaders.AUTHORIZATION, getUserIdToken())
                .header(NAV_CALL_ID, getCallId())
                .header(NAV_CONSUMER_TOKEN, getUserIdToken())

                .header(NAV_CONSUMER_ID, "Dolly")
                .build(), JsonNode.class);
    }

    private static String getCallId() {
        return format("Dolly: %s", UUID.randomUUID().toString());
    }
}
