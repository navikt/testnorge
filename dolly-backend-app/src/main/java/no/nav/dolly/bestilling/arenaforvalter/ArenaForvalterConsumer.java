package no.nav.dolly.bestilling.arenaforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Service
@RequiredArgsConstructor
public class ArenaForvalterConsumer {

    private static final String ARENAFORVALTER_BRUKER = "/api/v1/bruker";
    private static final String ARENAFORVALTER_DAGPENGER = "/api/v1/dagpenger";
    private static final String ARENAFORVALTER_ENVIRONMENTS = "/api/v1/miljoe";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "arena_getIdent" })
    public ResponseEntity getIdent(String ident) {
        return restTemplate.exchange(RequestEntity.get(
                        URI.create(format("%s%s?filter-personident=%s", providersProps.getArenaForvalter().getUrl(), ARENAFORVALTER_BRUKER, ident)))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .build(), ArenaArbeidssokerBruker.class);
    }

    @Timed(name = "providers", tags = { "operation", "arena_deleteIdent" })
    public ResponseEntity deleteIdent(String ident, String environment) {
        return restTemplate.exchange(RequestEntity.delete(
                        URI.create(format("%s%s?miljoe=%s&personident=%s", providersProps.getArenaForvalter().getUrl(), ARENAFORVALTER_BRUKER, environment, ident)))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .build(), JsonNode.class);
    }

    @Timed(name = "providers", tags = { "operation", "arena_postBruker" })
    public ResponseEntity<ArenaNyeBrukereResponse> postArenadata(ArenaNyeBrukere arenaNyeBrukere) {
        return restTemplate.exchange(RequestEntity.post(
                        URI.create(providersProps.getArenaForvalter().getUrl() + ARENAFORVALTER_BRUKER))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .body(arenaNyeBrukere), ArenaNyeBrukereResponse.class);
    }

    @Timed(name = "providers", tags = { "operation", "arena_postDagpenger" })
    public ResponseEntity<ArenaNyeDagpengerResponse> postArenaDagpenger(ArenaDagpenger arenaDagpenger) {
        return restTemplate.exchange(RequestEntity.post(
                        URI.create(providersProps.getArenaForvalter().getUrl() + ARENAFORVALTER_DAGPENGER))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .body(arenaDagpenger), ArenaNyeDagpengerResponse.class);
    }

    @Timed(name = "providers", tags = { "operation", "arena_getEnvironments" })
    public ResponseEntity<List> getEnvironments() {
        return restTemplate.exchange(RequestEntity.get(
                        URI.create(providersProps.getArenaForvalter().getUrl() + ARENAFORVALTER_ENVIRONMENTS))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .build(), List.class);
    }
}
