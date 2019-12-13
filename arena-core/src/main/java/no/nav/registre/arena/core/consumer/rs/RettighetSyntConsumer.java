package no.nav.registre.arena.core.consumer.rs;

import org.springframework.beans.factory.annotation.Autowired;
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
import no.nav.registre.arena.core.consumer.rs.responses.rettighet.NyRettighet;

@Component
public class RettighetSyntConsumer {

    private static final String TYPE_KODE_O = "O";
    private static final String UTFALL_JA = "JA";

    private final RestTemplate restTemplate;

    private final Random rand;

    private UriTemplate arenaAapUrl;
    private UriTemplate arenaAapUngUfoerUrl;
    private UriTemplate arenaAapTvungenForvaltningUrl;
    private UriTemplate arenaAapFritakMeldekortUrl;

    @Autowired public RettighetSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            Random rand,
            @Value("${arena-aap.rest-api.url}") String arenaAapServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.arenaAapUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap");
        this.arenaAapUngUfoerUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/aaungufor");
        this.arenaAapTvungenForvaltningUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/aatfor");
        this.arenaAapFritakMeldekortUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/fri_mk");
        this.rand = rand;
    }

    public List<NyRettighet> syntetiserRettighetAap(int antallMeldinger) {
        RequestEntity postRequest = createPostRequest(arenaAapUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyRettighet>>() {
        }).getBody();
    }

    public List<NyRettighet> syntetiserRettighetUngUfoer(int antallMeldinger) {
        RequestEntity postRequest = createPostRequest(arenaAapUngUfoerUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyRettighet>>() {
        }).getBody();
    }

    public List<NyRettighet> syntetiserRettighetTvungenForvaltning(int antallMeldinger) {
        RequestEntity postRequest = createPostRequest(arenaAapTvungenForvaltningUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyRettighet>>() {
        }).getBody();
    }

    public List<NyRettighet> syntetiserRettighetFritakMeldekort(int antallMeldinger) {
        RequestEntity postRequest = createPostRequest(arenaAapFritakMeldekortUrl, antallMeldinger);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<NyRettighet>>() {
        }).getBody();
    }

    private RequestEntity createPostRequest(UriTemplate uri, int antallMeldinger) {
        List<RettighetSyntRequest> requester = new ArrayList<>();
        for (int i = 0; i < antallMeldinger; i++) {
            requester.add(RettighetSyntRequest.builder()
                    .typeKode(TYPE_KODE_O)
                    .utfall(UTFALL_JA)
                    .startDato(LocalDate.now().minusMonths(rand.nextInt(12)))
                    .build());
        }
        return RequestEntity
                .post(uri.expand())
                .body(requester);
    }
}
