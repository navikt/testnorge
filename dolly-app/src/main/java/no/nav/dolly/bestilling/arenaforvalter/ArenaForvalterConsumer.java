package no.nav.dolly.bestilling.arenaforvalter;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.HttpEntity;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArenaForvalterConsumer {

    private static final String ARENAFORVALTER_BRUKER = "/api/v1/bruker";
    private static final String ARENAFORVALTER_ENVIRONMENTS = "/api/v1/miljoe";
    private static final String KILDE = "Dolly";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    private static String getCallId() {
        return "Dolly: " + UUID.randomUUID().toString();
    }

    public ResponseEntity<ArenaArbeidssokerBruker> getIdent(String ident) {
        return restTemplate.exchange(RequestEntity.get(
                URI.create(format("%s%s?filter-personident=%s", providersProps.getArenaForvalter().getUrl(), ARENAFORVALTER_BRUKER, ident)))
                .header(HEADER_NAV_CALL_ID, getCallId())
                .header(HEADER_NAV_CONSUMER_ID, KILDE)
                .build(), ArenaArbeidssokerBruker.class);
    }

    public void deleteIdent(String ident, String environment) {
        restTemplate.exchange(RequestEntity.delete(
                URI.create(format("%s%s?miljoe=%s&personident=%s", providersProps.getArenaForvalter().getUrl(), ARENAFORVALTER_BRUKER, environment, ident)))
                .header(HEADER_NAV_CALL_ID, getCallId())
                .header(HEADER_NAV_CONSUMER_ID, KILDE)
                .build(), JsonNode.class);
    }

    public ResponseEntity<ArenaArbeidssokerBruker> postArenadata(ArenaNyeBrukere arenaNyeBrukere) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getArenaForvalter().getUrl() + ARENAFORVALTER_BRUKER))
                .header(HEADER_NAV_CALL_ID, getCallId())
                .header(HEADER_NAV_CONSUMER_ID, KILDE)
                .body(arenaNyeBrukere), ArenaArbeidssokerBruker.class);
    }

    public ResponseEntity<List> getEnvironments() {
        return restTemplate.exchange(RequestEntity.get(
                URI.create(providersProps.getArenaForvalter().getUrl() + ARENAFORVALTER_ENVIRONMENTS))
                .header(HEADER_NAV_CALL_ID, getCallId())
                .header(HEADER_NAV_CONSUMER_ID, KILDE)
                .build(), List.class);
    }
}
