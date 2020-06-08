package no.nav.registre.arena.core.consumer.rs;

import static com.google.common.base.Strings.isNullOrEmpty;
import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.EIER;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CONSUMER_ID;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.NyBruker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;

@Component
@Slf4j
public class BrukereArenaForvalterConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate postBrukere;
    private UriTemplate hentBrukere;
    private UriTemplate slettBrukere;

    public BrukereArenaForvalterConsumer(
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
                .contentType(MediaType.APPLICATION_JSON)
                .header(CALL_ID, NAV_CALL_ID)
                .header(CONSUMER_ID, NAV_CONSUMER_ID)
                .body(Collections.singletonMap("nyeBrukere", nyeBrukere));

        return restTemplate.exchange(postRequest, NyeBrukereResponse.class).getBody();
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

        var baseUrl = new StringBuilder(hentBrukere.toString()).append("?");
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
                .header(CALL_ID, NAV_CALL_ID)
                .header(CONSUMER_ID, NAV_CONSUMER_ID)
                .build();
        var response = restTemplate.exchange(getRequest, NyeBrukereResponse.class).getBody();
        if (response != null) {
            return gaaGjennomSider(refinedUrl, response.getAntallSider(), response.getArbeidsoekerList().size());
        } else {
            return new ArrayList<>();
        }
    }

    private List<Arbeidsoeker> gaaGjennomSider(
            String baseUri,
            int antallSider,
            int initialLength
    ) {
        List<Arbeidsoeker> arbeidssoekere = new ArrayList<>(antallSider * initialLength);
        var hentBrukerePage = new UriTemplate(baseUri + "page={page}");

        for (int page = 0; page < antallSider; page++) {
            var getRequest = RequestEntity.get(hentBrukerePage.expand(page))
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .build();
            var response = restTemplate.exchange(getRequest, NyeBrukereResponse.class).getBody();
            if (response != null) {
                arbeidssoekere.addAll(response.getArbeidsoekerList());
            }
        }

        return arbeidssoekere;
    }

    @Timed(value = "testnroge.arena.resource.latency", extraTags = { "operation", "arena-forvalteren" })
    public Boolean slettBruker(
            String personident,
            String miljoe
    ) {
        var deleteRequest = RequestEntity.delete(slettBrukere.expand(miljoe, personident))
                .header(CALL_ID, NAV_CALL_ID)
                .header(CONSUMER_ID, NAV_CONSUMER_ID).build();
        log.info("Sletter ident {} fra Arena Forvalter i miljø {}.", personident.replaceAll("[\r\n]", ""), miljoe.replaceAll("[\r\n]", ""));
        var response = restTemplate.exchange(deleteRequest, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Kunne ikke slette bruker. Status: {}", response.getStatusCode());
            return false;
        }

        return true;
    }
}