package no.nav.registre.arena.core.consumer.rs;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils;

@Component
@DependencyOn(value = "nais-synthdata-arena-tiltak", external = true)
public class TiltakSyntConsumer {

    private RestTemplate restTemplate;
    private ConsumerUtils consumerUtils;

    private UriTemplate arenaTiltaksdeltakelseUrl;
    private UriTemplate arenaTiltakspengerUrl;
    private UriTemplate arenaBarnetilleggUrl;
    private UriTemplate arenaDeltakerstatusUrl;
    private UriTemplate arenaTiltaksaktivitetUrl;

    public TiltakSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            ConsumerUtils consumerUtils,
            @Value("${synt-arena-tiltak.rest-api.url}") String arenaTiltakServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.consumerUtils = consumerUtils;
        this.arenaTiltaksdeltakelseUrl = new UriTemplate(arenaTiltakServerUrl + "/v1/arena/tiltak/deltakelse");
        this.arenaTiltakspengerUrl = new UriTemplate(arenaTiltakServerUrl + "/v1/arena/tiltak/basi");
        this.arenaBarnetilleggUrl = new UriTemplate(arenaTiltakServerUrl + "/v1/arena/tiltak/btil");
        this.arenaDeltakerstatusUrl = new UriTemplate(arenaTiltakServerUrl + "/v1/arena/tiltak/deltakerstatus");
        this.arenaTiltaksaktivitetUrl = new UriTemplate(arenaTiltakServerUrl + "/v1/arena/tiltak/aktivitet");
    }

    public List<NyttVedtakTiltak> opprettTiltaksdeltakelse(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaTiltaksdeltakelseUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTiltak>>() {
        }).getBody();
    }

    public List<NyttVedtakTiltak> opprettTiltakspenger(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaTiltakspengerUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTiltak>>() {
        }).getBody();
    }

    public List<NyttVedtakTiltak> opprettBarnetillegg(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaBarnetilleggUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTiltak>>() {
        }).getBody();
    }

    public List<NyttVedtakTiltak> opprettDeltakerstatus(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaDeltakerstatusUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTiltak>>() {
        }).getBody();
    }

    public List<NyttVedtakTiltak> opprettTiltaksaktivitet(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaTiltaksaktivitetUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakTiltak>>() {
        }).getBody();
    }
}
