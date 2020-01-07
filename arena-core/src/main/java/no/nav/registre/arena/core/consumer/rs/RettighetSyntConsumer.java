package no.nav.registre.arena.core.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import no.nav.registre.arena.core.consumer.rs.request.RettighetSyntRequest;
import no.nav.registre.arena.domain.historikk.Vedtakshistorikk;
import no.nav.registre.arena.domain.rettighet.NyRettighet;

@Component
public class RettighetSyntConsumer {

    private static final String VEDTAK_TYPE_KODE_O = "O";
    private static final String UTFALL_JA = "JA";

    private RestTemplate restTemplate;

    private Random rand;

    private UriTemplate arenaVedtakshistorikkUrl;
    private UriTemplate arenaAapUrl;
    private UriTemplate arenaAapUngUfoerUrl;
    private UriTemplate arenaAapTvungenForvaltningUrl;
    private UriTemplate arenaAapFritakMeldekortUrl;

    public RettighetSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            Random rand,
            @Value("${synt-arena.rest-api.url}") String arenaAapServerUrl,
            @Value("${synt-arena-vedtakshistorikk.rest-api.url}") String arenaVedtakshistorikkServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.arenaVedtakshistorikkUrl = new UriTemplate(arenaVedtakshistorikkServerUrl + "/v1/arena/vedtakshistorikk");
        this.arenaAapUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap");
        this.arenaAapUngUfoerUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/aaungufor");
        this.arenaAapTvungenForvaltningUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/aatfor");
        this.arenaAapFritakMeldekortUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/fri_mk");
        this.rand = rand;
    }

    public List<Vedtakshistorikk> syntetiserVedtakshistorikk(int antallIdenter) {
        List<LocalDate> oppstartsdatoer = new ArrayList<>();

        for (int i = 0; i < antallIdenter; i++) {
            oppstartsdatoer.add(LocalDate.now().minusMonths(rand.nextInt(120)));
        }

        var postRequest = RequestEntity.post(arenaVedtakshistorikkUrl.expand()).body(oppstartsdatoer);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<Vedtakshistorikk>>() {
        }).getBody();
    }

    public List<NyRettighet> syntetiserRettighetAap(int antallMeldinger) {
        var postRequest = createPostRequest(arenaAapUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyRettighet>>() {
        }).getBody();
    }

    public List<NyRettighet> syntetiserRettighetUngUfoer(int antallMeldinger) {
        var postRequest = createPostRequest(arenaAapUngUfoerUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyRettighet>>() {
        }).getBody();
    }

    public List<NyRettighet> syntetiserRettighetTvungenForvaltning(int antallMeldinger) {
        var postRequest = createPostRequest(arenaAapTvungenForvaltningUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyRettighet>>() {
        }).getBody();
    }

    public List<NyRettighet> syntetiserRettighetFritakMeldekort(int antallMeldinger) {
        var postRequest = createPostRequest(arenaAapFritakMeldekortUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyRettighet>>() {
        }).getBody();
    }

    private RequestEntity createPostRequest(UriTemplate uri, int antallMeldinger) {
        List<RettighetSyntRequest> requester = new ArrayList<>();
        for (int i = 0; i < antallMeldinger; i++) {
            LocalDate startDato = LocalDate.now().minusMonths(rand.nextInt(12));
            LocalDate sluttDato = startDato.plusDays(rand.nextInt(365));
            requester.add(RettighetSyntRequest.builder()
                    .fraDato(startDato)
                    .tilDato(sluttDato)
                    .utfall(UTFALL_JA)
                    .vedtakTypeKode(VEDTAK_TYPE_KODE_O)
                    .vedtakDato(startDato)
                    .build());
        }
        return RequestEntity
                .post(uri.expand())
                .body(requester);
    }
}
