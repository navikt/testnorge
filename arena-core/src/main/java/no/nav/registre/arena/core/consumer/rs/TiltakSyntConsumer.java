package no.nav.registre.arena.core.consumer.rs;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils;

@Component
public class TiltakSyntConsumer {

    private RestTemplate restTemplate;
    private ConsumerUtils consumerUtils;

    private UriTemplate arenaTiltaksdeltakelseUrl;
    private UriTemplate arenaTiltakspengerUrl;
    private UriTemplate arenaBarnetilleggUrl;

    public TiltakSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            ConsumerUtils consumerUtils,
            @Value("${synt-arena-tiltak.rest-api.url}") String arenaTiltakServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.consumerUtils = consumerUtils;
        this.arenaTiltaksdeltakelseUrl = new UriTemplate(arenaTiltakServerUrl + "/v1/arena/tiltak/deltakelse/{antallIdenter}");
        this.arenaTiltakspengerUrl = new UriTemplate(arenaTiltakServerUrl + "/v1/arena/tiltak/basi");
        this.arenaBarnetilleggUrl = new UriTemplate(arenaTiltakServerUrl + "/v1/arena/tiltak/btil");
    }

    public List<NyttVedtakTiltak> opprettTiltaksdeltakelse(int antallMeldinger) {
        var getRequest = RequestEntity.get(arenaTiltaksdeltakelseUrl.expand(antallMeldinger)).build();
        return restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<NyttVedtakTiltak>>() {
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
}
