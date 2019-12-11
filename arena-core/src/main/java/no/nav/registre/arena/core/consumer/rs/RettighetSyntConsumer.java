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
import no.nav.registre.arena.core.consumer.rs.responses.rettighet.UngUfoer.UngUfoerSyntResponse;

@Component
public class RettighetSyntConsumer {

    private static final String TYPE_KODE_O = "O";
    private static final String UTFALL_JA = "JA";

    private final RestTemplate restTemplate;

    private final Random rand;

    private UriTemplate arenaAapUrl;
    private UriTemplate arenaAapUngUfoerUrl;
    private UriTemplate arenaAapAatforUrl;
    private UriTemplate arenaAapFriMkUrl;

    @Autowired public RettighetSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            Random rand,
            @Value("${arena-aap.rest-api.url}") String arenaAapServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.arenaAapUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap");
        this.arenaAapUngUfoerUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/aaungufor");
        this.arenaAapAatforUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/aatfor");
        this.arenaAapFriMkUrl = new UriTemplate(arenaAapServerUrl + "/v1/arena/aap/fri_mk");
        this.rand = rand;
    }

    public List<UngUfoerSyntResponse> syntetiserRettighetUngUfoer(int antallMeldinger) {
        List<RettighetSyntRequest> requester = new ArrayList<>();
        for (int i = 0; i < antallMeldinger; i++) {
            requester.add(RettighetSyntRequest.builder()
                    .typeKode(TYPE_KODE_O)
                    .utfall(UTFALL_JA)
                    .startDato(LocalDate.now().minusMonths(rand.nextInt(12)))
                    .build());
        }
        RequestEntity postRequest = RequestEntity
                .post(arenaAapUngUfoerUrl.expand())
                .body(requester);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<UngUfoerSyntResponse>>() {
        }).getBody();
    }
}
