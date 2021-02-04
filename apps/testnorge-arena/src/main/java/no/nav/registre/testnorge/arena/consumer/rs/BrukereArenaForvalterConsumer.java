package no.nav.registre.testnorge.arena.consumer.rs;

import static com.google.common.base.Strings.isNullOrEmpty;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.command.DeleteArenaBrukerCommand;
import no.nav.registre.testnorge.arena.consumer.rs.command.GetArenaBrukereCommand;
import no.nav.registre.testnorge.arena.consumer.rs.command.PostArenaBrukerCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.arena.consumer.rs.util.ArbeidssoekerCacheUtil;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.NyBruker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;

@Component
@Slf4j
@DependencyOn(value = "arena-forvalteren", external = true)
public class BrukereArenaForvalterConsumer {

    private final WebClient webClient;

    private ArbeidssoekerCacheUtil arbeidssoekerCacheUtil;

    private UriTemplate hentBrukere;


    public BrukereArenaForvalterConsumer(
            ArbeidssoekerCacheUtil arbeidssoekerCacheUtil,
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(arenaForvalterServerUrl).build();
        this.hentBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker");
        this.arbeidssoekerCacheUtil = arbeidssoekerCacheUtil;
    }

    @Timed(value = "testnorge.arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public NyeBrukereResponse sendTilArenaForvalter(
            List<NyBruker> nyeBrukere
    ) {
        return new PostArenaBrukerCommand(nyeBrukere, webClient).call();
    }

    @Timed(value = "testnorge.arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public List<Arbeidsoeker> hentArbeidsoekere(
            String personident,
            String eier,
            String miljoe
    ) {

        var refinedUrl = getFullstendigHentArbeidsoekereUrl(personident, eier, miljoe);

        var cachedeArbeidssoekere = arbeidssoekerCacheUtil.hentArbeidssoekere(refinedUrl);
        if (cachedeArbeidssoekere != null && !cachedeArbeidssoekere.isEmpty()) {
            return cachedeArbeidssoekere;
        }

        var response = new GetArenaBrukereCommand(getQueryParams(personident, eier, miljoe, null), webClient).call();

        if (response != null) {
            var arbeidssoekere = gaaGjennomSider(personident, eier, miljoe, response.getAntallSider(), response.getArbeidsoekerList().size());
            arbeidssoekerCacheUtil.oppdaterCache(refinedUrl, arbeidssoekere);
            return arbeidssoekere;
        } else {
            return new ArrayList<>();
        }
    }

    private List<Arbeidsoeker> gaaGjennomSider(
            String personident,
            String eier,
            String miljoe,
            int antallSider,
            int initialLength
    ) {
        List<Arbeidsoeker> arbeidssoekere = new ArrayList<>(antallSider * initialLength);

        for (int page = 0; page < antallSider; page++) {
            var queryParams = getQueryParams(personident, eier, miljoe, page + "");
            var response = new GetArenaBrukereCommand(queryParams, webClient).call();
            if (response != null) {
                arbeidssoekere.addAll(response.getArbeidsoekerList());
            }
        }

        return arbeidssoekere;
    }


    private MultiValueMap<String, String> getQueryParams(
            String personident,
            String eier,
            String miljoe,
            String page
    ) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (!isNullOrEmpty(eier)) {
            queryParams.add("filter-eier", eier);
        }
        if (!isNullOrEmpty(miljoe)) {
            queryParams.add("filter-miljoe", miljoe);
        }
        if (!isNullOrEmpty(personident)) {
            queryParams.add("filter-personident", personident);
        }
        if (!isNullOrEmpty(page)) {
            queryParams.add("page", page);
        }
        return queryParams;
    }

    private String getFullstendigHentArbeidsoekereUrl(
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
        return baseUrl.toString();
    }

    @Timed(value = "testnorge.arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public boolean slettBruker(
            String personident,
            String miljoe
    ) {
        return new DeleteArenaBrukerCommand(personident, miljoe, webClient).call();
    }
}