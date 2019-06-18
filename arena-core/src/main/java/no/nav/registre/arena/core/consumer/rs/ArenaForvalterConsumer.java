package no.nav.registre.arena.core.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.provider.rs.requests.NyeBrukereRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;


@Component
@Slf4j
public class ArenaForvalterConsumer {

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_LEGG_TIL_NYE_BRUKERE = new ParameterizedTypeReference<Object>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate leggTilNyeBrukereUrl;

    public ArenaForvalterConsumer(@Value("@{arena-forvalteren.rest-api.url}") String arenaForvalterUrl) {
        this.leggTilNyeBrukereUrl = new UriTemplate(arenaForvalterUrl + "/v1/bruker");
    }

    public ResponseEntity leggTilSyntetiserteBrukereIArenaForvalter(NyeBrukereRequest nyeBrukereRequest) {
        RequestEntity postRequest = RequestEntity.post(leggTilNyeBrukereUrl.expand())
                .header("Nav-Call-Id", "orkestratoren")
                .header("Nav-Consumer-Id", "orkestratoren")
                .body(nyeBrukereRequest);
        try {
            return restTemplate.exchange(postRequest, RESPONSE_TYPE_LEGG_TIL_NYE_BRUKERE);
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke legge til nye brukere i Arena Forvalter.\n{}", e);
            return ResponseEntity.status(e.getStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
        }
    }



}
