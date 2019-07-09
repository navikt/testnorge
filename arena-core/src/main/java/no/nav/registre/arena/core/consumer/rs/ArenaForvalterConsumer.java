package no.nav.registre.arena.core.consumer.rs;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.domain.NyeBrukereList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class ArenaForvalterConsumer {

    private static final String EIER = "Dolly";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String NAV_CALL_ID = "ORKESTRATOREN";
    private static final String NAV_CONSUMER_ID = "ORKESTRATOREN";
    private int PAGE = 0;


    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate postBrukere;
    private UriTemplate hentBrukere;
    private UriTemplate slettBrukere;

    // TODO: er det nødvendig å ha eier med på post-requesten? Bør den også være med på hentBrukere?
    public ArenaForvalterConsumer(@Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl) {
        this.postBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker?eier=" + EIER);
        this.hentBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker");
        this.slettBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker?miljoe={miljoe}&personident={personident}");
    }


    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public StatusFraArenaForvalterResponse sendTilArenaForvalter(NyeBrukereList nyeBrukere) {

        RequestEntity postRequest = RequestEntity.post(postBrukere.expand())
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                .body(createJsonRequestBody(nyeBrukere));

        ResponseEntity<StatusFraArenaForvalterResponse> response =
                restTemplate.exchange(postRequest, StatusFraArenaForvalterResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Ugyldig brukeroppsett. Kunne ikke opprette nye brukere. Status: {}", response.getStatusCode());
            return null;
        }

        return response.getBody();
    }

    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public StatusFraArenaForvalterResponse hentBrukere() {
        RequestEntity getRequest = RequestEntity.get(hentBrukere.expand())
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                .build();
        ResponseEntity<StatusFraArenaForvalterResponse> response =
                restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Status {}", response.getStatusCode());
            return null;
        }

        if (response.getBody() == null) {
            log.error("Kunne ikke hente response body fra Arena Forvalteren.");
            return null;
        }

        return response.getBody();
    }

    public List<String> hentEksisterendeIdenter() {

        StatusFraArenaForvalterResponse response = hentBrukere();

        if (response == null) {
            log.error("Fant ingen eksisterende identer.");
            return new ArrayList<>();
        }

        return response.getArbeidsokerList().stream()
                .map(Arbeidsoker::getPersonident)
                .collect(Collectors.toList());
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


    private String createJsonRequestBody(NyeBrukereList nyeBrukere) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(sdf);

            return objectMapper.writeValueAsString(nyeBrukere);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }

        return "";
    }
}