package no.nav.registre.arena.core.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.core.utility.NetworkUtil;
import no.nav.registre.arena.domain.NyBruker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


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
    private UriTemplate hentBrukerePage;

    // TODO: er det nødvendig å ha eier med på post-requesten? Bør den også være med på hentBrukere?
    public ArenaForvalterConsumer(@Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl) {
        this.postBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker?eier=" + EIER);
        this.hentBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker");
        this.hentBrukerePage = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker?page={page}");
        this.slettBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker?miljoe={miljoe}&personident={personident}");
    }


    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public List<Arbeidsoker> sendTilArenaForvalter(List<NyBruker> nyeBrukere) {

        RequestEntity postRequest = RequestEntity.post(postBrukere.expand())
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                .body(Collections.singletonMap("nyeBrukere", nyeBrukere));
        ResponseEntity<StatusFraArenaForvalterResponse> response = createValidResponseEntity(postRequest);
        if (response == null)
            return new ArrayList<>();

        return response.getBody().getArbeidsokerList();
    }

    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public List<Arbeidsoker> hentBrukere() {
        RequestEntity getRequest = RequestEntity.get(hentBrukere.expand())
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                .build();
        ResponseEntity<StatusFraArenaForvalterResponse> response = createValidResponseEntity(getRequest);
        if (response == null)
            return new ArrayList<>();

        List<Arbeidsoker> responseList =
                new ArrayList<>(response.getBody().getAntallSider() * response.getBody().getArbeidsokerList().size());

        for (int page = 1; page <= response.getBody().getAntallSider(); page++) {
            getRequest = RequestEntity.get(hentBrukerePage.expand(page))
                    .header("Nav-Call-Id", NAV_CALL_ID)
                    .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                    .build();
            response = restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);

            if (!NetworkUtil.validResponse(response)) {
                log.warn("Kunne ikke hente response body fra Arena Frovalteren på side {}. Returnerer response fra foregående sider.", page);
                break;
            } else
                responseList.addAll(response.getBody().getArbeidsokerList());
        }

        return responseList;
    }

    public List<String> hentEksisterendeIdenter() {

        List<Arbeidsoker> arbeisokere = hentBrukere();

        if (arbeisokere.isEmpty()) {
            log.error("Fant ingen eksisterende identer.");
            return new ArrayList<>();
        }

        return arbeisokere.stream().map(Arbeidsoker::getPersonident).collect(Collectors.toList());
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

    private ResponseEntity<StatusFraArenaForvalterResponse> createValidResponseEntity(RequestEntity getRequest) {
        ResponseEntity<StatusFraArenaForvalterResponse> response =
                restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);

        if(!NetworkUtil.validResponse(response)) {
            log.error("Status: {}", response.getStatusCode());
            log.error("Body: {}", response.getBody());
            return null;
        }

        return response;
    }
}