package no.nav.dolly.bestilling.arenaforvalter;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.properties.ProvidersProps;

@Service
@AllArgsConstructor
public class ArenaForvalterConsumer {

    private static final String ARENAFORVALTER_BRUKER = "/api/v1/bruker";
    private static final String ARENAFORVALTER_ENVIRONMENTS = "/api/v1/miljoe";
    private static final String KILDE = "Dolly";

    private RestTemplate restTemplate;
    private ProvidersProps providersProps;

    public ResponseEntity getIdent(String ident) {
        return restTemplate.exchange(RequestEntity.get(
                URI.create(format("%s%s?filter-personident=%s", providersProps.getArenaForvalter().getUrl(), ARENAFORVALTER_BRUKER, ident)))
                .header(HEADER_NAV_CALL_ID, getCallId())
                .header(HEADER_NAV_CONSUMER_ID, KILDE)
                .build(), ArenaArbeidssokerBruker.class);
    }

    public ResponseEntity deleteIdent(String ident, String environment) {
        return restTemplate.exchange(RequestEntity.delete(
                URI.create(format("%s%s?miljoe=%s&personident=%s", providersProps.getArenaForvalter().getUrl(), ARENAFORVALTER_BRUKER, environment, ident)))
                .header(HEADER_NAV_CALL_ID, getCallId())
                .header(HEADER_NAV_CONSUMER_ID, KILDE)
                .build(), JsonNode.class);
    }

    public ResponseEntity<ArenaNyeBrukereResponse> postArenadata(ArenaNyeBrukere arenaNyeBrukere) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getArenaForvalter().getUrl() + ARENAFORVALTER_BRUKER))
                .header(HEADER_NAV_CALL_ID, getCallId())
                .header(HEADER_NAV_CONSUMER_ID, KILDE)
                .body(arenaNyeBrukere), ArenaNyeBrukereResponse.class);
    }

    public ResponseEntity<List> getEnvironments() {
        return restTemplate.exchange(RequestEntity.get(
                URI.create(providersProps.getArenaForvalter().getUrl() + ARENAFORVALTER_ENVIRONMENTS))
                .header(HEADER_NAV_CALL_ID, getCallId())
                .header(HEADER_NAV_CONSUMER_ID, KILDE)
                .build(), List.class);
    }

    private static String getCallId() {
        return "Dolly: " + UUID.randomUUID().toString();
    }
}
