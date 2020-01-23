package no.nav.registre.arena.core.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.arena.domain.vedtak.NyttVedtakTillegg;

@Component
public class TilleggSyntConsumer {

    private RestTemplate restTemplate;
    private ConsumerUtils consumerUtils;

    private UriTemplate arenaTilleggLaeremidlerUrl;
    private UriTemplate arenaTilleggBoutgiftUrl;
    private UriTemplate arenaTilleggFlyttingUrl;
    private UriTemplate arenaTilleggHjemreiseUrl;
    private UriTemplate arenaTilleggReisestoenadArbeidssoekereUrl;
    private UriTemplate arenaTilleggBoutgifterArbeidssoekereUrl;
    private UriTemplate arenaTilleggDagligReiseUrl;
    private UriTemplate arenaTilleggReiseObligatoriskSamlingUrl;

    public TilleggSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            ConsumerUtils consumerUtils,
            @Value("${synt-arena-tillegg.rest-api.url}") String arenaTilleggServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.consumerUtils = consumerUtils;
        this.arenaTilleggLaeremidlerUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/laeremidler");
        this.arenaTilleggBoutgiftUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/boutgift");
        this.arenaTilleggFlyttingUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/flytting");
        this.arenaTilleggHjemreiseUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/reise_aktivitet_og_hjemreiser");
        this.arenaTilleggReisestoenadArbeidssoekereUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/reisestonad_til_arbeidssokere");
        this.arenaTilleggBoutgifterArbeidssoekereUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/boutgifter_arbeidssokere");
        this.arenaTilleggDagligReiseUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/daglig_reise");
        this.arenaTilleggReiseObligatoriskSamlingUrl = new UriTemplate(arenaTilleggServerUrl + "/v1/arena/tilleggsstonad/reise_til_obligatorisk_samling");
    }

    public List<NyttVedtakTillegg> opprettLaeremidler(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaTilleggLaeremidlerUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTillegg>>() {
        }).getBody();
    }

    public List<NyttVedtakTillegg> opprettBoutgifter(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaTilleggBoutgiftUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTillegg>>() {
        }).getBody();
    }

    public List<NyttVedtakTillegg> opprettFlytting(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaTilleggFlyttingUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTillegg>>() {
        }).getBody();
    }

    public List<NyttVedtakTillegg> opprettHjemreise(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaTilleggHjemreiseUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTillegg>>() {
        }).getBody();
    }

    public List<NyttVedtakTillegg> opprettReisestoenadArbeidssoekere(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaTilleggReisestoenadArbeidssoekereUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTillegg>>() {
        }).getBody();
    }

    public List<NyttVedtakTillegg> opprettBoutgifterArbeidssoekere(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaTilleggBoutgifterArbeidssoekereUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTillegg>>() {
        }).getBody();
    }

    public List<NyttVedtakTillegg> opprettDagligReise(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaTilleggDagligReiseUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTillegg>>() {
        }).getBody();
    }

    public List<NyttVedtakTillegg> opprettReiseObligatoriskSamling(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaTilleggReiseObligatoriskSamlingUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTillegg>>() {
        }).getBody();
    }
}
