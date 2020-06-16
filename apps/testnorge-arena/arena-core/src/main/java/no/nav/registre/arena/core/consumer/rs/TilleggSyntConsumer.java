package no.nav.registre.arena.core.consumer.rs;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.time.LocalDate;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils;

@Component
@DependencyOn(value = "nais-synthdata-arena-tilleggsstonad", external = true)
public class TilleggSyntConsumer {

    private RestTemplate restTemplate;
    private ConsumerUtils consumerUtils;

    private UriTemplate arenaTilleggBoutgiftUrl;
    private UriTemplate arenaTilleggDagligReiseUrl;
    private UriTemplate arenaTilleggFlyttingUrl;
    private UriTemplate arenaTilleggLaeremidlerUrl;
    private UriTemplate arenaTilleggHjemreiseUrl;
    private UriTemplate arenaTilleggReiseObligatoriskSamlingUrl;

    private UriTemplate arenaTilleggTilsynBarnUrl;
    private UriTemplate arenaTilleggTilsynFamiliemedlemmerUrl;
    private UriTemplate arenaTilleggTilsynBarnArbeidssoekereUrl;
    private UriTemplate arenaTilleggTilsynFamiliemedlemmerArbeidssoekereUrl;

    private UriTemplate arenaTilleggBoutgifterArbeidssoekereUrl;
    private UriTemplate arenaTilleggDagligReiseArbeidssoekereUrl;
    private UriTemplate arenaTilleggFlyttingArbeidssoekereUrl;
    private UriTemplate arenaTilleggLaeremidlerArbeidssoekereUrl;
    private UriTemplate arenaTilleggHjemreiseArbeidssoekereUrl;
    private UriTemplate arenaTilleggReiseObligatoriskSamlingArbeidssoekereUrl;
    private UriTemplate arenaTilleggReisestoenadArbeidssoekereUrl;

    public static final LocalDate ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT = LocalDate.of(2020, 02, 29);

    public TilleggSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            ConsumerUtils consumerUtils,
            @Value("${synt-arena-tillegg.rest-api.url}") String arenaTilleggServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.consumerUtils = consumerUtils;
        this.arenaTilleggBoutgiftUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/boutgift");
        this.arenaTilleggDagligReiseUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/daglig_reise");
        this.arenaTilleggFlyttingUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/flytting");
        this.arenaTilleggLaeremidlerUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/laeremidler");
        this.arenaTilleggHjemreiseUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/reise_aktivitet_og_hjemreiser");
        this.arenaTilleggReiseObligatoriskSamlingUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/reise_til_obligatorisk_samling");

        this.arenaTilleggTilsynBarnUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/tilsyn_barn");
        this.arenaTilleggTilsynFamiliemedlemmerUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/tilsyn_familiemedlemmer");
        this.arenaTilleggTilsynBarnArbeidssoekereUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/tilsyn_barn_arbeidssoker");
        this.arenaTilleggTilsynFamiliemedlemmerArbeidssoekereUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/tilsyn_familiemedlemmer_arbeidssokere");

        this.arenaTilleggBoutgifterArbeidssoekereUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/boutgifter_arbeidssokere");
        this.arenaTilleggDagligReiseArbeidssoekereUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/daglig_reise_arbeidssoker");
        this.arenaTilleggFlyttingArbeidssoekereUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/flytting_arbeidssokere");
        this.arenaTilleggLaeremidlerArbeidssoekereUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/laeremidler_arbeidssokere");
        this.arenaTilleggHjemreiseArbeidssoekereUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/reise_aktivitet_og_hjemreiser_arbeidssokere");
        this.arenaTilleggReiseObligatoriskSamlingArbeidssoekereUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/reise_til_obligatorisk_samling_arbeidssokere");
        this.arenaTilleggReisestoenadArbeidssoekereUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/reisestonad_til_arbeidssokere");
    }

    public List<NyttVedtakTillegg> opprettBoutgifter(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggBoutgiftUrl);
    }

    public List<NyttVedtakTillegg> opprettDagligReise(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggDagligReiseUrl);
    }

    public List<NyttVedtakTillegg> opprettFlytting(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggFlyttingUrl);
    }

    public List<NyttVedtakTillegg> opprettLaeremidler(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggLaeremidlerUrl);
    }

    public List<NyttVedtakTillegg> opprettHjemreise(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggHjemreiseUrl);
    }

    public List<NyttVedtakTillegg> opprettReiseObligatoriskSamling(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggReiseObligatoriskSamlingUrl);
    }

    public List<NyttVedtakTillegg> opprettTilsynBarn(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggTilsynBarnUrl);
    }

    public List<NyttVedtakTillegg> opprettTilsynFamiliemedlemmer(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggTilsynFamiliemedlemmerUrl, ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT);
    }

    public List<NyttVedtakTillegg> opprettTilsynBarnArbeidssoekere(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggTilsynBarnArbeidssoekereUrl);
    }

    public List<NyttVedtakTillegg> opprettTilsynFamiliemedlemmerArbeidssoekere(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggTilsynFamiliemedlemmerArbeidssoekereUrl);
    }

    public List<NyttVedtakTillegg> opprettBoutgifterArbeidssoekere(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggBoutgifterArbeidssoekereUrl);
    }

    public List<NyttVedtakTillegg> opprettDagligReiseArbeidssoekere(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggDagligReiseArbeidssoekereUrl);
    }

    public List<NyttVedtakTillegg> opprettFlyttingArbeidssoekere(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggFlyttingArbeidssoekereUrl);
    }

    public List<NyttVedtakTillegg> opprettLaeremidlerArbeidssoekere(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggLaeremidlerArbeidssoekereUrl);
    }

    public List<NyttVedtakTillegg> opprettHjemreiseArbeidssoekere(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggHjemreiseArbeidssoekereUrl);
    }

    public List<NyttVedtakTillegg> opprettReiseObligatoriskSamlingArbeidssoekere(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggReiseObligatoriskSamlingArbeidssoekereUrl);
    }

    public List<NyttVedtakTillegg> opprettReisestoenadArbeidssoekere(int antallMeldinger) {
        return opprettTilleggstoenad(antallMeldinger, arenaTilleggReisestoenadArbeidssoekereUrl);
    }

    private List<NyttVedtakTillegg> opprettTilleggstoenad(
            int antallMeldinger,
            UriTemplate uri
    ) {
        var postRequest = consumerUtils.createPostRequest(uri, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTillegg>>() {
        }).getBody();
    }

    private List<NyttVedtakTillegg> opprettTilleggstoenad(
            int antallMeldinger,
            UriTemplate uri,
            LocalDate startDatoLimit
    ) {
        var postRequest = consumerUtils.createPostRequest(uri, antallMeldinger, startDatoLimit);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTillegg>>() {
        }).getBody();
    }
}
