package no.nav.registre.arena.core.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
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

        ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(postRequest, StatusFraArenaForvalterResponse.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            log.error("Kunne ikke sende brukere til Arena Forvalteren. Ingen respons.\nStatus: {}\nBody: {}",
                    response.getStatusCode(), response.getBody());
            return new ArrayList<>();
        }

        return response.getBody().getArbeidsokerList();
    }

    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public List<Arbeidsoker> hentBrukere() {
        RequestEntity getRequest = RequestEntity.get(hentBrukere.expand())
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                .build();

        ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            log.error("Kunne ikke hente brukere fra Arena Forvalteren.\nStatus: {}\nBody: {}",
                    response.getStatusCode(), response.getBody());
            return new ArrayList<>();
        }

        List<Arbeidsoker> responseList =
                new ArrayList<>(response.getBody().getAntallSider() * response.getBody().getArbeidsokerList().size());

        for (int page = 1; page <= response.getBody().getAntallSider(); page++) {
            getRequest = RequestEntity.get(hentBrukerePage.expand(page))
                    .header("Nav-Call-Id", NAV_CALL_ID)
                    .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                    .build();
            response = restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);


            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Status: {}", response.getStatusCode());
                break;
            }
            if (response.getBody() == null) {
                log.warn("Kunne ikke hente response body fra Arena Frovalteren på side {}. Returnerer response fra foregående sider.", page);
                break;
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
}