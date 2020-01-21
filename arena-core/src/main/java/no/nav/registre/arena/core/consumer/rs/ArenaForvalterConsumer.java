package no.nav.registre.arena.core.consumer.rs;

import static com.google.common.base.Strings.isNullOrEmpty;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
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

import no.nav.registre.arena.core.consumer.rs.responses.NyeBrukereResponse;
import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.domain.brukere.Arbeidsoeker;
import no.nav.registre.arena.domain.brukere.NyBruker;

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

    public ArenaForvalterConsumer(
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.postBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker?eier=" + EIER);
        this.hentBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker");
        this.slettBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker?miljoe={miljoe}&personident={personident}");
    }

    @Timed(value = "testnorge.arena.resource.latency", extraTags = { "operation", "arena-forvalteren" })
    public NyeBrukereResponse sendTilArenaForvalter(
            List<NyBruker> nyeBrukere
    ) {
        var postRequest = RequestEntity.post(postBrukere.expand())
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                .body(Collections.singletonMap("nyeBrukere", nyeBrukere));

        var response = restTemplate.exchange(postRequest, StatusFraArenaForvalterResponse.class);
        if (invalidResponse(response)) {
            log.warn("Kunne ikke sende arbeidsoekere til Arena Forvalteren på addresse:\n{}.\nStatus: {}\nBody: {}",
                    postRequest.toString(), response.getStatusCode(), response.getBody());
            return new NyeBrukereResponse();
        }

        var formatertResponse = new NyeBrukereResponse();
        formatertResponse.setArbeidsoekerList(response.getBody().getArbeidsokerList());
        formatertResponse.setNyBrukerFeilList(response.getBody().getNyBrukerFeilList());
        return formatertResponse;
    }

    @Timed(value = "testnorge.arena.resource.latency", extraTags = { "operation", "arena-forvalteren" })
    public List<Arbeidsoeker> hentArbeidsoekere(
            String personident,
            String eier,
            String miljoe
    ) {

        Map<String, String> filters = new HashMap<>();
        if (!isNullOrEmpty(eier)) {
            filters.put("filter-eier", eier);
        }
        if (!isNullOrEmpty(miljoe)) {
            filters.put("filter-miljoe", miljoe);
        }
        if (!isNullOrEmpty(personident)) {
            filters.put("filter-personident", personident);
        }

        var baseUrl = new StringBuilder(hentBrukere.toString() + "?");
        for (var filter : filters.entrySet()) {
            if (filter.getValue() != null) {
                baseUrl.append(filter.getKey())
                        .append('=')
                        .append(filter.getValue())
                        .append('&');
            }
        }

        return hentFiltrerteArbeidsoekere(baseUrl.toString());
    }

    private List<Arbeidsoeker> hentFiltrerteArbeidsoekere(
            String refinedUrl
    ) {
        var uri = new UriTemplate(refinedUrl);

        var getRequest = RequestEntity.get(uri.expand())
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                .build();
        var response = restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);
        if (invalidResponse(response)) {
            log.info("Kunne ikke hente arbeidsøkere fra Arena Forvalteren på addresse:\n{}\nStatus: {}\nBody: {}",
                    getRequest.toString(), response.getStatusCode(), response.getBody());
            return new ArrayList<>();
        }

        return gaaGjennomSider(refinedUrl, response.getBody().getAntallSider(), response.getBody().getArbeidsokerList().size());
    }

    private List<Arbeidsoeker> gaaGjennomSider(
            String baseUri,
            int antallSider,
            int initialLength
    ) {
        List<Arbeidsoeker> responseList = new ArrayList<>(antallSider * initialLength);
        var hentBrukerePage = new UriTemplate(baseUri + "page={page}");

        for (int page = 0; page < antallSider; page++) {
            var getRequest = RequestEntity.get(hentBrukerePage.expand(page)).header("Nav-Call-Id", NAV_CALL_ID).header("Nav-Consumer-Id", NAV_CONSUMER_ID).build();

            var response = restTemplate.exchange(getRequest, StatusFraArenaForvalterResponse.class);
            if (invalidResponse(response)) {
                log.warn("Kunne ikke hente arbeidsøkere fra Arena Forvalteren på addresse:\n{}\nStatus: {}\nBody: {}",
                        getRequest.toString(), response.getStatusCode(), response.getBody());
                return responseList;
            }

            responseList.addAll(response.getBody().getArbeidsokerList());
        }

        return responseList;
    }

    @Timed(value = "testnroge.arena.resource.latency", extraTags = { "operation", "arena-forvalteren" })
    public Boolean slettBrukerSuccessful(
            String personident,
            String miljoe
    ) {
        var deleteRequest = RequestEntity.delete(slettBrukere.expand(miljoe, personident))
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID).build();
        log.info("Sletter ident {} fra Arena Forvalter i miljø {}.", personident.replaceAll("[\r\n]", ""), miljoe.replaceAll("[\r\n]", ""));
        var response = restTemplate.exchange(deleteRequest, String.class);

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