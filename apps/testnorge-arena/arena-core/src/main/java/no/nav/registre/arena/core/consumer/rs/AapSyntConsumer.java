package no.nav.registre.arena.core.consumer.rs;

import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.UTFALL_JA;
import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.VEDTAK_TYPE_KODE_O;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.request.RettighetSyntRequest;
import no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependenciesOn;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;

@Component
@DependenciesOn(value = {
        @DependencyOn(value = "nais-synthdata-arena-aap", external = true),
        @DependencyOn(value = "nais-synthdata-arena-vedtakshistorikk", external = true)
})
public class AapSyntConsumer {


    public static final LocalDate ARENA_AAP_UNG_UFOER_DATE_LIMIT = LocalDate.of(2020, 1, 31);

    private RestTemplate restTemplate;
    private ConsumerUtils consumerUtils;

    private UriTemplate arenaAapUrl;
    private UriTemplate arenaAap115Url;
    private UriTemplate arenaAapUngUfoerUrl;
    private UriTemplate arenaAapTvungenForvaltningUrl;
    private UriTemplate arenaAapFritakMeldekortUrl;

    public AapSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            ConsumerUtils consumerUtils,
            @Value("${synt-arena.rest-api.url}") String arenaAapServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.consumerUtils = consumerUtils;
        this.arenaAapUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap");
        this.arenaAap115Url = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/11_5");
        this.arenaAapUngUfoerUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/aaungufor");
        this.arenaAapTvungenForvaltningUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/aatfor");
        this.arenaAapFritakMeldekortUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/fri_mk");
    }

    public List<NyttVedtakAap> syntetiserRettighetAap(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaAapUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakAap>>() {
        }).getBody();
    }

    public List<NyttVedtakAap> syntetiserRettighetAap115(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaAap115Url, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakAap>>() {
        }).getBody();
    }

    public List<NyttVedtakAap> syntetiserRettighetAap115(
            LocalDate startDato,
            LocalDate sluttDato
    ) {
        RequestEntity<ArrayList<RettighetSyntRequest>> postRequest = RequestEntity.post(arenaAap115Url.expand()).body(
                new ArrayList<>(Collections.singletonList(
                        RettighetSyntRequest.builder()
                                .fraDato(startDato)
                                .tilDato(sluttDato)
                                .utfall(UTFALL_JA)
                                .vedtakTypeKode(VEDTAK_TYPE_KODE_O)
                                .vedtakDato(startDato)
                                .build()
                ))
        );
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakAap>>() {
        }).getBody();
    }

    public List<NyttVedtakAap> syntetiserRettighetUngUfoer(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaAapUngUfoerUrl, antallMeldinger, ARENA_AAP_UNG_UFOER_DATE_LIMIT);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakAap>>() {
        }).getBody();
    }

    public List<NyttVedtakAap> syntetiserRettighetTvungenForvaltning(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaAapTvungenForvaltningUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakAap>>() {
        }).getBody();
    }

    public List<NyttVedtakAap> syntetiserRettighetFritakMeldekort(int antallMeldinger) {
        var postRequest = consumerUtils.createPostRequest(arenaAapFritakMeldekortUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyttVedtakAap>>() {
        }).getBody();
    }
}
