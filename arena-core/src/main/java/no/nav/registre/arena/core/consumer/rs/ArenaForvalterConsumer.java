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
import java.util.List;


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


    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public List<Arbeidsoeker> sendTilArenaForvalter(List<NyBruker> nyeBrukere) {

        RequestEntity postRequest = RequestEntity.post(postBrukere.expand())
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                .body(Collections.singletonMap("nyeBrukere", nyeBrukere));

        ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(postRequest, StatusFraArenaForvalterResponse.class);
        return response.getBody().getArbeidsokerList();
    }

    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public List<Arbeidsoeker> hentArbeidsoekereFilter(List<String> personidenter) {
        UriTemplate url = new UriTemplate(hentBrukere.toString() + "?filter-personident={personident}");
        List<Arbeidsoeker> hentedeArbeidsoekere = new ArrayList<>(personidenter.size());

        for (String personident : personidenter) {
            RequestEntity getRequest = RequestEntity.get(url.expand(personident)).header("Nav-Call-Id", NAV_CALL_ID).header("Nav-Consumer-Id", NAV_CONSUMER_ID).build();
            ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);
            String expandedUrl = url.expand(personident).toString() + "&";
            hentedeArbeidsoekere.addAll(gaaGjennomSider(expandedUrl, response.getBody().getAntallSider(), response.getBody().getArbeidsokerList().size()));
        }

        return hentedeArbeidsoekere;
    }

    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public List<Arbeidsoeker> hentArbeidsoekere() {
        RequestEntity getRequest = RequestEntity.get(hentBrukere.expand()).header("Nav-Call-Id", NAV_CALL_ID).header("Nav-Consumer-Id", NAV_CONSUMER_ID).build();
        ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);
        return gaaGjennomSider(hentBrukere.toString() + "?", response.getBody().getAntallSider(), response.getBody().getArbeidsokerList().size());
    }

    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public List<Arbeidsoeker> hentArbeidsoekere(String eier, String miljoe, String personident) {
        String url = hentBrukere.toString() + "?";

        int numArgs = 0;
        if (!("".equals(personident) || personident == null)) {
            url += "filter-personident=" + personident;
            numArgs++;
        }
        if (!("".equals(miljoe) || miljoe == null)) {
            if (numArgs > 0) {
                url += "&";
            }
            url += "filter-miljoe=" + miljoe;
            numArgs++;
        }
        if (!("".equals(eier) || eier == null)) {
            if (numArgs > 0) {
                url += "&";
            }
            url += "filter-eier=" + eier;
            numArgs++;
        }
        RequestEntity getRequest = RequestEntity.get(new UriTemplate(url).expand()).header("Nav-Call-Id", NAV_CALL_ID).header("Nav-Consumer-Id", NAV_CONSUMER_ID).build();
        ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);
        return gaaGjennomSider(url + ((numArgs > 0) ? "&" : ""), response.getBody().getAntallSider(), response.getBody().getArbeidsokerList().size());
    }

    private List<Arbeidsoeker> gaaGjennomSider(String baseUri, int antallSider, int initialLength) {

        List<Arbeidsoeker> responseList = new ArrayList<>(antallSider * initialLength);
        UriTemplate hentBrukerePage = new UriTemplate(baseUri + "page={page}");

        for (int page = 1; page <= antallSider; page++) {
            RequestEntity getRequest = RequestEntity.get(hentBrukerePage.expand(page)).header("Nav-Call-Id", NAV_CALL_ID).header("Nav-Consumer-Id", NAV_CONSUMER_ID).build();
            ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);
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
}