package no.nav.dolly.bestilling.arena;

import static java.lang.String.format;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import no.nav.dolly.domain.resultset.arenastub.ArenaNyeBrukereRequest;
import no.nav.dolly.domain.resultset.arenastub.RsArenadata;
import no.nav.dolly.properties.ProvidersProps;

@Service
public class ArenaStubConsumer {

    private static final String ARENA_BRUKER_URL = "/api/v1/bruker";
    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvidersProps providersProps;

    public ResponseEntity deleteIdent(String ident) {
        return restTemplate.exchange(RequestEntity.delete(
                URI.create(format("%s%s?personident=%s", providersProps.getArenaForvalter().getUrl(), ARENA_BRUKER_URL, ident)))
                .header(NAV_CALL_ID, getCallId())
                .header(NAV_CONSUMER_ID, "Dolly")
                .build(), JsonNode.class);
    }

    public ResponseEntity postArenadata(RsArenadata arenadata) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getArenaForvalter().getUrl() + ARENA_BRUKER_URL))
                .header(NAV_CALL_ID, getCallId())
                .header(NAV_CONSUMER_ID, "Dolly")
                .body(ArenaNyeBrukereRequest.builder()
                        .nyeBrukere(Collections.singletonList(arenadata)).build()), JsonNode.class);
    }

    private static String getCallId() {
        return format("Dolly: %s", UUID.randomUUID().toString());
    }
}
