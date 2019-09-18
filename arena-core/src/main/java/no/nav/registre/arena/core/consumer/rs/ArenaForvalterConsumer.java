package no.nav.registre.arena.core.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.domain.Arbeidsoeker;
import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.domain.NyBruker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Component
@Slf4j
public class ArenaForvalterConsumer {

    private static final String EIER = "ORKESTRATOREN";
    private static final String NAV_CALL_ID = "ORKESTRATOREN";
    private static final String NAV_CONSUMER_ID = "ORKESTRATOREN";

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate postBrukere;
    private UriTemplate hentBrukere;
    private UriTemplate slettBrukere;


    public ArenaForvalterConsumer(@Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl) {
        this.postBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker?eier=" + EIER);
        this.hentBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker");
        this.slettBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker?miljoe={miljoe}&personident={personident}");
    }


    @Timed(value = "testnorge.arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public List<Arbeidsoeker> sendTilArenaForvalter(List<NyBruker> nyeBrukere) {

        RequestEntity postRequest = RequestEntity.post(postBrukere.expand())
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                .body(Collections.singletonMap("nyeBrukere", nyeBrukere));

        ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(postRequest, StatusFraArenaForvalterResponse.class);
        if (invalidResponse(response)) {
            log.info("Kunne ikke sende arbeidsoekere til Arena Forvalteren på addresse:\n{}.\nStatus: {}\nBody: {}",
                    postRequest.toString(), response.getStatusCode(), response.getBody());
            return new ArrayList<>();
        }
        return response.getBody().getArbeidsokerList();
    }

    @Timed(value = "testnorge.arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public List<Arbeidsoeker> hentArbeidsoekere(String personident, String eier, String miljoe) {

        Map<String, String> filters = new HashMap<>();
        if (!"".equals(eier)) {
            filters.put("filter-eier", eier);
        }
        if (!"".equals(miljoe)) {
            filters.put("filter-miljoe", miljoe);
        }
        if (!"".equals(personident)) {
            filters.put("filter-personident", personident);
        }

        StringBuilder baseUrl = new StringBuilder(hentBrukere.toString() + "?");
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            baseUrl.append(entry.getKey())
                    .append('=')
                    .append(entry.getValue())
                    .append('&');
        }

        return hentFiltrerteArbeidsoekere(baseUrl.toString());
    }

    private List<Arbeidsoeker> hentFiltrerteArbeidsoekere(String refinedUrl) {
        UriTemplate uri = new UriTemplate(refinedUrl);

        RequestEntity getRequest = RequestEntity.get(uri.expand()).header("Nav-Call-Id", NAV_CALL_ID).header("Nav-Consumer-Id", NAV_CONSUMER_ID).build();
        ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);
        if (invalidResponse(response)) {
            log.info("Kunne ikke hente arbeidsøkere fra Arena Forvalteren på addresse:\n{}\nStatus: {}\nBody: {}",
                        getRequest.toString(), response.getStatusCode(), response.getBody());
            return new ArrayList<>();
        }

        return gaaGjennomSider(refinedUrl, response.getBody().getAntallSider(), response.getBody().getArbeidsokerList().size());
    }

    private List<Arbeidsoeker> gaaGjennomSider(String baseUri, int antallSider, int initialLength) {

        List<Arbeidsoeker> responseList = new ArrayList<>(antallSider * initialLength);
        UriTemplate hentBrukerePage = new UriTemplate(baseUri + "page={page}");

        for (int page = 0; page < antallSider; page++) {
            RequestEntity getRequest = RequestEntity.get(hentBrukerePage.expand(page)).header("Nav-Call-Id", NAV_CALL_ID).header("Nav-Consumer-Id", NAV_CONSUMER_ID).build();
            ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);
            if (invalidResponse(response)) {
                log.info("Kunne ikke hente arbeidsøkere fra Arena Forvalteren på addresse:\n{}\nStatus: {}\nBody: {}",
                        getRequest.toString(), response.getStatusCode(), response.getBody());
                return responseList;
            }
            responseList.addAll(response.getBody().getArbeidsokerList());
        }

        return responseList;
    }

    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public Boolean slettBrukerSuccessful(String personident, String miljoe) {
        RequestEntity deleteRequest = RequestEntity.delete(slettBrukere.expand(miljoe, personident))
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID).build();

        ResponseEntity response = restTemplate.exchange(deleteRequest, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Kunne ikke slette bruker. Status: {}", response.getStatusCode());
            return false;
        }

        return true;
    }

    private Boolean invalidResponse(ResponseEntity<?> response) {
        return (Objects.isNull(response.getBody()) || response.getStatusCode() != HttpStatus.OK);
    }
}